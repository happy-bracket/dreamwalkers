package ru.substancial.dreamwalkers.screen

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.objects.CircleMapObject
import com.badlogic.gdx.maps.objects.EllipseMapObject
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import ru.substancial.dreamwalkers.Core
import ru.substancial.dreamwalkers.bodies.DummyBody
import ru.substancial.dreamwalkers.controls.TheController
import ru.substancial.dreamwalkers.dev.SuperFlat
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent
import ru.substancial.dreamwalkers.ecs.entity.CreateDummy
import ru.substancial.dreamwalkers.ecs.entity.CreateLuna
import ru.substancial.dreamwalkers.ecs.entity.InputEntity
import ru.substancial.dreamwalkers.ecs.entity.createWeapon
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.system.*
import ru.substancial.dreamwalkers.utilities.ClearScreen
import ru.substancial.dreamwalkers.utilities.TerrainBody

class GameScreen(private val core: Core) : ScreenAdapter() {

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(16f, 9f, camera)

    private val world = World(Vector2(0f, -10f), false)
    private val debugRenderer = Box2DDebugRenderer(
            true,
            true,
            false,
            true,
            true,
            true
    )

    private val controller = TheController()

    private val cameraSystem = CameraSystem(camera)
    private val renderSystem = DebugRenderSystem(world, camera, debugRenderer)
    private val controlsSystem = ControlsSystem(controller)
    private val aerialSystem = AerialSystem(world)
    private val lunaBodySystem = LunaBodySystem()
    private val positionSystem = PositionSystem()
    private val weaponSystem = WeaponSystem()
    private val lunaLookSystem = LunaLookSystem()
    private val decelerationSystem = DecelerationSystem()
    private val aiSystem = AiSystem(world)

    private val engine = Engine()
            .apply {
                addEntity(InputEntity())

                addSystem(cameraSystem)
                addSystem(renderSystem)
                addSystem(controlsSystem)
                addSystem(aerialSystem)
                addSystem(lunaBodySystem)
                addSystem(positionSystem)
                addSystem(weaponSystem)
                addSystem(lunaLookSystem)
                addSystem(decelerationSystem)
                addSystem(aiSystem)
            }

    init {
        Controllers.addListener(controller)

        val map = TmxMapLoader().load("assets/levels/fixedtest.tmx")
        val terrain = TerrainBody(map, world)

        val spawnPoint = map.layers["spawn_point"]
                .objects.first()
                .let { it as EllipseMapObject }
                .ellipse.let {
                    Vector2(it.x / 16, it.y / 16)
                }

        val luna = world.CreateLuna(spawnPoint)
        engine.addEntity(luna)
        engine.addEntity(createWeapon(world, luna.extract<BodyComponent>().body))
        val lunaBody = engine.getEntitiesFor(Family.all(LunaComponent::class.java).get()).first()
                .extract<BodyComponent>()
                .body
        core.commandExecutor.currentEngine = engine

        Gdx.app.log("terrain", terrain.position.toString())
        Gdx.app.log("luna", lunaBody.position.toString())
    }

    override fun render(delta: Float) {
        ClearScreen()
        world.step(1 / 60f, 6, 2)
        engine.update(delta)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        world.dispose()
        debugRenderer.dispose()
        Controllers.clearListeners()
        core.commandExecutor.currentEngine = null
    }

}