package ru.substancial.dreamwalkers.ecs.system

import box2dLight.ConeLight
import box2dLight.PointLight
import box2dLight.RayHandler
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.esotericsoftware.spine.SkeletonRenderer
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.files.DreamwalkersAssetManager
import ru.substancial.dreamwalkers.utilities.RegisteringSystem
import ru.substancial.dreamwalkers.utilities.extractIdentityInto
import ru.substancial.dreamwalkers.utilities.extractPositionInto
import ru.substancial.dreamwalkers.utilities.justListen
import java.util.*
import java.util.regex.Pattern

class RenderSystem(private val assetManager: DreamwalkersAssetManager) : RegisteringSystem() {

    private val cameraEntity by singular(Family.all(CameraComponent::class.java).get())
    private val drawables by listener(
        Family.one(SpriteComponent::class.java, SkeletonComponent::class.java).get(),
        LinkedList(),
        ::addEntity,
        { m, e -> m.removeIf { drawable -> drawable.entity == e } },
        { m -> m.clear() }
    )
    private val handlerEntity by singular(Family.all(RayHandlerComponent::class.java).get())

    private val levelLoaded by justListen(
        Family.all(LevelComponent::class.java).get(),
        onAdded = ::inflateDrawingEntities
    )

    private val batch = PolygonSpriteBatch()
    private val renderer = SkeletonRenderer().also { it.setPremultipliedAlpha(true) }

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        handlerEntity.extract<RayHandlerComponent>().handler.setAmbientLight(0f, 0f, 0f, 1f)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        val camera = cameraEntity.extract<CameraComponent>().camera

        batch.projectionMatrix = camera.combined
        batch.begin()
        var negLayersStart = -1
        for (i in drawables.indices) {
            val drawable = drawables[i]
            if (drawable.layer >= 0)
                draw(drawable.entity, batch)
            else {
                negLayersStart = i
                break
            }
        }
        batch.end()

        val handler = handlerEntity.extract<RayHandlerComponent>().handler
        handler.setCombinedMatrix(camera)
        handler.updateAndRender()

        if (negLayersStart != -1) {
            batch.begin()
            for (i in negLayersStart..drawables.lastIndex) {
                val drawable = drawables[i]
                draw(drawable.entity, batch)
            }
            batch.end()
        }

    }

    private fun inflateDrawingEntities(levelEntity: Entity) {
        val map = levelEntity.extract<LevelComponent>().level.map

        val pattern = Pattern.compile("visual([+-](?:0|[1-9]\\d*))$")

        val mapAtlas = map.properties["atlas"] as String?

        for (layer in map.layers) {
            val matcher = pattern.matcher(layer.name)
            if (matcher.find()) {
                val layerIx = matcher.group(1).toInt()
                val layerAtlas = layer.properties["atlas"] as String?
                layer.objects.forEach { obj ->
                    val objAtlas = obj.properties["atlas"] as String?
                    val targetAtlas = assetManager[(objAtlas ?: layerAtlas) ?: mapAtlas, TextureAtlas::class.java]
                    inflateObject(targetAtlas, obj as RectangleMapObject, layerIx)
                }
            }
        }

        inflateLights(map)
    }

    private fun inflateObject(atlas: TextureAtlas, obj: RectangleMapObject, layerIx: Int) {
        val type = (obj.properties["drawable_type"] ?: return) as Int
        val entity = Entity()
        obj.extractIdentityInto(entity)
        obj.extractPositionInto(entity)
        entity.add(DrawingLayerComponent(layerIx))
        when (type) {
            0 -> {
                val drawableName = obj.properties["drawable_name"] as String? ?: return
                val drawableComponent = SpriteComponent(
                    atlas.findRegion(drawableName),
                    obj.rectangle.width,
                    obj.rectangle.height
                )
                entity.add(drawableComponent)
            }
        }
        engine.addEntity(entity)
    }

    private fun inflateLights(map: TiledMap) {
        val lightObjectsLayer = map.layers["lights"] ?: return
        // 0 - point, 1 - cone
        val handler = handlerEntity.extract<RayHandlerComponent>().handler
        lightObjectsLayer.objects.forEach {
            inflateLight(it, handler)
        }
    }

    private fun inflateLight(obj: MapObject, handler: RayHandler) {
        val entity = Entity()
        obj.extractIdentityInto(entity)
        val position = obj.extractPositionInto(entity)
        val rays = obj.properties["light_rays"] as Int
        val distance = obj.properties["light_distance"] as Float
        val color = obj.properties["light_color"] as Color
        val light = when (obj.properties["light_type"] as Int) {
            0 -> PointLight(handler, rays, color, distance, position.xy.x, position.xy.y)
            1 -> {
                val direction = obj.properties["light_direction"] as Float
                val coneDegree = obj.properties["light_cone_degree"] as Float
                ConeLight(handler, rays, color, distance, position.xy.x, position.xy.y, direction, coneDegree)
            }
            else -> throw IllegalArgumentException("unknown light type.")
        }
        entity.add(LightComponent(light))
        engine.addEntity(entity)
    }

    private fun addEntity(cache: LinkedList<ExposedLayerDrawableEntity>, entity: Entity) {
        val entityLayer = entity.maybeExtract<DrawingLayerComponent>()?.drawingLayer ?: 0
        val represented = ExposedLayerDrawableEntity(entityLayer, entity)
        for (i in cache.indices) {
            val currentLayer = cache[i].layer
            if (entityLayer >= currentLayer) {
                cache.add(i, represented)
                return
            }
        }
        cache.addLast(represented)
    }

    private fun draw(entity: Entity, batch: PolygonSpriteBatch) {
        val position = entity.maybeExtract<PositionComponent>()?.xy ?: Vector2.Zero
        entity.maybeExtract<SpriteComponent>()?.let { spriteComponent ->
            batch.draw(spriteComponent.region, position.x, position.y + spriteComponent.height, spriteComponent.width, spriteComponent.height)
        }
        entity.maybeExtract<SkeletonComponent>()?.let { skeletonComponent ->
            val skeleton = skeletonComponent.skeleton
            renderer.draw(batch, skeleton)
        }
    }

    private class ExposedLayerDrawableEntity(
        val layer: Int,
        val entity: Entity
    )

}
