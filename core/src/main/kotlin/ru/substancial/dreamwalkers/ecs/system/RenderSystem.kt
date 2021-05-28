package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.assets.loaders.AssetLoader
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.MapObject
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.maybeExtract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem
import ru.substancial.dreamwalkers.utilities.justListen
import java.util.*
import java.util.regex.Pattern

class RenderSystem : RegisteringSystem() {

    private val cameraEntity by singular(Family.all(CameraComponent::class.java).get())
    private val drawables by listener(
        Family.one(SpriteComponent::class.java).get(),
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

    private val batch = SpriteBatch()

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


        for (layer in map.layers) {
            val matcher = pattern.matcher(layer.name)
            if (matcher.find()) {
                val layerIx = matcher.group(1).toInt()
                layer.objects.forEach { obj ->
//                    inflateObject(obj, layerIx)
                }
            }
        }

    }

    private fun inflateObject(parentAtlas: TextureAtlas, obj: MapObject, layerIx: Int) {
        val type = (obj.properties["drawable_type"] ?: return) as Int
        val id = (obj.properties["object_id"]) as? String?
        val entity = Entity()
        entity.add(DrawingLayerComponent(layerIx))
        if (id != null)
            entity.add(IdentityComponent(id))
        when (type) {
            0 -> {
                val drawableName = (obj.properties["drawable_name"] ?: return) as String
                val drawableComponent = SpriteComponent(parentAtlas.findRegion(drawableName).let(TextureAtlas::AtlasSprite))
                entity.add(drawableComponent)
            }
        }
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

    private fun draw(entity: Entity, batch: SpriteBatch) {
        entity.maybeExtract<SpriteComponent>()?.sprite?.draw(batch)
    }

    private class ExposedLayerDrawableEntity(
        val layer: Int,
        val entity: Entity
    )

}
