package ru.substancial.dreamwalkers.screen

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
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
import ru.substancial.dreamwalkers.ecs.entity.EntitySpawner
import ru.substancial.dreamwalkers.ecs.other.HitMediator
import ru.substancial.dreamwalkers.ecs.system.*
import ru.substancial.dreamwalkers.level.*
import ru.substancial.dreamwalkers.utilities.ClearScreen
import ru.substancial.dreamwalkers.utilities.IdentityRegistry

class GameScreen(
        private val core: Core,
        private val scenarioPath: String,
        scenarioName: String,
        saveFile: SaveFile?
) : HasDisplayScreenAdapter() {

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

    private val scenarioHolder: ScenarioHolder
    private val registry: IdentityRegistry = IdentityRegistry()

    private val cameraSystem = CameraSystem(camera)
    private val renderSystem = DebugRenderSystem(world, camera, debugRenderer)
    private val collisionSystem: CollisionSystem
    private val lunaBodySystem = LunaBodySystem(controller)
    private val positionSystem = PositionSystem()
    private val weaponSystem = WeaponSystem(controller)
    private val lunaLookSystem = LunaLookSystem(controller)
    private val decelerationSystem = DecelerationSystem()
    private val aiSystem = AiSystem(world)
    private val worldSystem = WorldSystem(world)
    private val vitalitySystem = VitalitySystem()
    private val registrySystem = RegistrySystem(registry)
    private val hurtboxSystem = HurtboxFollowSystem()
    private val stuckSystem = ImpaleSystem(world)

    private val engine = Engine()
            .apply {
                addSystem(worldSystem)
                addSystem(cameraSystem)
                addSystem(lunaBodySystem)
                addSystem(positionSystem)
                addSystem(weaponSystem)
                addSystem(lunaLookSystem)
                addSystem(decelerationSystem)
                addSystem(aiSystem)
                addSystem(vitalitySystem)
                addSystem(registrySystem)
                addSystem(hurtboxSystem)
                addSystem(renderSystem)
                addSystem(stuckSystem)
            }

    init {
        Controllers.addListener(controller)
        core.commandExecutor.currentEngine = engine

        val interactor = GameScenarioCallbacks()
        scenarioHolder = ScenarioHolder("$scenarioPath/$scenarioName", interactor, engine, registry, EntitySpawner(world, engine))
        scenarioHolder.initialize(saveFile)

        collisionSystem = CollisionSystem(world, scenarioHolder, HitMediator(engine))
        engine.addSystem(collisionSystem)
    }

    override fun render(delta: Float) {
        ClearScreen()
        scenarioHolder.update(delta)
        engine.update(delta)
        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        world.dispose()
        debugRenderer.dispose()
        Controllers.clearListeners()
        core.commandExecutor.currentEngine = null
    }

    inner class GameScenarioCallbacks : ScenarioCallbacks {

        override fun loadLevel(name: String) {
            val levelFolder = Gdx.files.internal("$scenarioPath/$name")
            val levelFolderContents = levelFolder.list()

            val levelPath = levelFolderContents.first { it.extension() == "tmx" }

            val map = TmxMapLoader().load(levelPath.path(), TmxMapLoader.Parameters().apply { convertObjectToTileSpace = true })
            val loadedLevel = Level(map)
            loadedLevel.inflate(world, engine)
        }

        override fun gameOver(iconFile: String, title: String, description: String) {
            core.screen = GameOverScreen(
                    core,
                    iconFile,
                    title,
                    description
            )
        }

    }

}
