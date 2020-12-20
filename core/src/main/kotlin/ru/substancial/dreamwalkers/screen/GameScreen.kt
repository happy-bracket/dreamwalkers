package ru.substancial.dreamwalkers.screen

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ru.substancial.dreamwalkers.Core
import ru.substancial.dreamwalkers.controls.TheController
import ru.substancial.dreamwalkers.ecs.entity.Entities
import ru.substancial.dreamwalkers.ecs.entity.Luna
import ru.substancial.dreamwalkers.ecs.system.*
import ru.substancial.dreamwalkers.level.Level
import ru.substancial.dreamwalkers.utilities.ClearScreen

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

    private val stage = Stage(viewport)

    private val cameraSystem = CameraSystem(camera)
    private val renderSystem = DebugRenderSystem(world, camera, debugRenderer)
    private val aerialSystem = AerialSystem(world)
    private val lunaBodySystem = LunaBodySystem(controller)
    private val positionSystem = PositionSystem()
    private val weaponSystem = WeaponSystem(controller)
    private val lunaLookSystem = LunaLookSystem(controller)
    private val decelerationSystem = DecelerationSystem()
    private val aiSystem = AiSystem(world)

    private val engine = Engine()
            .apply {
                addSystem(cameraSystem)
                addSystem(renderSystem)
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

        val map = TmxMapLoader().load("assets/levels/observatory.tmx")
        val level = Level(map, 16)

        level.constructGround(world)

        Entities.Luna(
                level.playerSpawnPoint,
                engine,
                world
        )
        core.commandExecutor.currentEngine = engine
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