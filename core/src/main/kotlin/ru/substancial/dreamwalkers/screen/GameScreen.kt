package ru.substancial.dreamwalkers.screen

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import ru.substancial.dreamwalkers.controls.TheController
import ru.substancial.dreamwalkers.dev.SuperFlat
import ru.substancial.dreamwalkers.ecs.entity.CreateLuna
import ru.substancial.dreamwalkers.ecs.system.AerialSystem
import ru.substancial.dreamwalkers.ecs.system.CameraSystem
import ru.substancial.dreamwalkers.ecs.system.ControlsSystem
import ru.substancial.dreamwalkers.ecs.system.DebugRenderSystem
import ru.substancial.dreamwalkers.utilities.ClearScreen

class GameScreen : ScreenAdapter() {

    private val deadzone: Float = 0.3f

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(16f, 9f, camera)

    private val world = World(Vector2(0f, -10f), false)
            .apply {
                SuperFlat()
            }
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
    private val controlsSystem = ControlsSystem(camera, controller)
    private val aerialSystem = AerialSystem(world)

    private val engine = Engine()
            .apply {
                addEntity(world.CreateLuna())

                addSystem(cameraSystem)
                addSystem(renderSystem)
                addSystem(controlsSystem)
                addSystem(aerialSystem)
            }

    init {
        Controllers.addListener(controller)
    }

    override fun render(delta: Float) {
        ClearScreen()
        world.step(1/60f, 6, 2)
        engine.update(delta)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        world.dispose()
        debugRenderer.dispose()
        Controllers.clearListeners()
    }

}