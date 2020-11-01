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
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.entity.CreateLuna
import ru.substancial.dreamwalkers.ecs.entity.InputEntity
import ru.substancial.dreamwalkers.ecs.entity.createWeapon
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.system.*
import ru.substancial.dreamwalkers.utilities.ClearScreen

class GameScreen : ScreenAdapter() {

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
    private val controlsSystem = ControlsSystem(controller)
    private val aerialSystem = AerialSystem(world)
    private val lunaBodySystem = LunaBodySystem()
    private val positionSystem = PositionSystem()
    private val weaponSystem = WeaponSystem()
    private val lunaLookSystem = LunaLookSystem()

    private val engine = Engine()
            .apply {
                val luna = world.CreateLuna()
                addEntity(luna)
                addEntity(createWeapon(world, luna.extract<BodyComponent>().body))
                addEntity(InputEntity())

                addSystem(cameraSystem)
                addSystem(renderSystem)
                addSystem(controlsSystem)
                addSystem(aerialSystem)
                addSystem(lunaBodySystem)
                addSystem(positionSystem)
                addSystem(weaponSystem)
                addSystem(lunaLookSystem)
            }

    init {
        Controllers.addListener(controller)
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
    }

}