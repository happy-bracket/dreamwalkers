package ru.substancial.dreamwalkers.screen

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.EarClippingTriangulator
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ru.substancial.dreamwalkers.Core
import ru.substancial.dreamwalkers.controls.TheController
import ru.substancial.dreamwalkers.ecs.entity.EntitySpawner
import ru.substancial.dreamwalkers.ecs.system.*
import ru.substancial.dreamwalkers.level.Level
import ru.substancial.dreamwalkers.level.SaveFile
import ru.substancial.dreamwalkers.level.ScenarioCallbacks
import ru.substancial.dreamwalkers.level.ScenarioHolder
import ru.substancial.dreamwalkers.nightsedge.NightsEdgeLoader
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

    private val skin = Skin(Gdx.files.internal("assets/testskin/uiskin.json"))
    private val stage = Stage()

    private val scenarioHolder: ScenarioHolder
    private val registry: IdentityRegistry = IdentityRegistry()

    private val cameraSystem = CameraSystem(camera)
    private val renderSystem = DebugRenderSystem(world, camera, debugRenderer)
    private val lunaBodySystem = LunaBodySystem(controller)
    private val positionSystem = PositionSystem()
    private val weaponSystem = WeaponSystem(controller)
    private val lunaLookSystem = LunaLookSystem(controller)
    private val groundFrictionSystem = GroundFrictionSystem()
    private val aiSystem = AiSystem(world)
    private val worldSystem = WorldSystem(world)
    private val registrySystem = RegistrySystem(registry)
    private val hurtboxSystem = HurtboxFollowSystem()
    private val stuckSystem = ImpaleSystem(world)
    private val cooldownsSystem = CooldownsSystem()
    private val aerialSystem = AerialSystem()
    private val scenarioCollisionSystem: ScenarioCollisionSystem

    private val engine = Engine()

    init {
        Controllers.addListener(controller)
        core.commandExecutor.currentEngine = engine

        val root = Table()
        root.setFillParent(true)
        stage.addActor(root)

        root.row()

        val dashCooldown = ProgressBar(0f, 1f, 0.01f, false, skin)
        root.add(dashCooldown)

        dashCooldown.isVisible = false // temporal

        stage.isDebugAll = true

        val interactor = GameScenarioCallbacks()
        scenarioHolder = ScenarioHolder(
                "$scenarioPath/$scenarioName",
                interactor,
                engine, registry,
                EntitySpawner(
                        world, engine,
                        NightsEdgeLoader(EarClippingTriangulator(), TmxMapLoader(), world)
                ))

        scenarioCollisionSystem = ScenarioCollisionSystem(scenarioHolder)

        engine.apply {
            addSystem(weaponSystem)
            addSystem(lunaLookSystem)
            addSystem(groundFrictionSystem)
            addSystem(aiSystem)
            addSystem(registrySystem)
            addSystem(stuckSystem)
            addSystem(cooldownsSystem)
            addSystem(aerialSystem)
            addSystem(lunaBodySystem)
            addSystem(ForcesSystem())
            addSystem(worldSystem)
            addSystem(CollisionSystem(world))
            addSystem(DamageSystem())
            addSystem(scenarioCollisionSystem)
            addSystem(positionSystem)
            addSystem(hurtboxSystem)
            addSystem(InteractionSystem())
            addSystem(cameraSystem)
            addSystem(DisplaySystem(dashCooldown))
            addSystem(renderSystem)
            addSystem(DisposalSystem())
        }

        scenarioHolder.initialize(saveFile)
    }

    override fun render(delta: Float) {
        ClearScreen()
        scenarioHolder.update(delta)
        engine.update(delta)
        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        engine.removeAllEntities()
        engine.systems.forEach(engine::removeSystem)
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
            Gdx.app.log("egor2", "jopa")
            Gdx.app.postRunnable {
                core.screen = GameOverScreen(
                    core,
                    iconFile,
                    title,
                    description
                )
            }
        }

    }

}
