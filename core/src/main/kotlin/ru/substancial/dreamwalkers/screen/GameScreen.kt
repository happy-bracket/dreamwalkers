package ru.substancial.dreamwalkers.screen

import box2dLight.RayHandler
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.loaders.AssetLoader
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.EarClippingTriangulator
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ru.substancial.dreamwalkers.Assets
import ru.substancial.dreamwalkers.Core
import ru.substancial.dreamwalkers.controls.TheController
import ru.substancial.dreamwalkers.ecs.component.CameraComponent
import ru.substancial.dreamwalkers.ecs.component.LevelComponent
import ru.substancial.dreamwalkers.ecs.component.RayHandlerComponent
import ru.substancial.dreamwalkers.ecs.entity.EntitySpawner
import ru.substancial.dreamwalkers.ecs.system.*
import ru.substancial.dreamwalkers.files.DreamwalkersAssetManager
import ru.substancial.dreamwalkers.files.NightsEdgeLoader
import ru.substancial.dreamwalkers.level.*
import ru.substancial.dreamwalkers.utilities.ClearScreen
import ru.substancial.dreamwalkers.utilities.EntityOf
import ru.substancial.dreamwalkers.utilities.IdentityRegistry

class GameScreen(
    private val assetManager: DreamwalkersAssetManager,
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

    private val rayHandler = RayHandler(world)

    private val controller = TheController()

    private val skin: Skin = assetManager[Assets.Skin]
    private val stage = Stage()

    private val scenarioHolder: ScenarioHolder
    private val registry: IdentityRegistry = IdentityRegistry()

    private val engine = Engine()

    private val levelLoader = LevelLoader(scenarioPath, assetManager)
    private var stateReady = false

    init {
        Controllers.addListener(controller)
        core.commandExecutor.currentEngine = engine

        // region UI
        val root = Table()
        root.setFillParent(true)
        stage.addActor(root)

        root.row()

        val dashCooldown = ProgressBar(0f, 1f, 0.01f, false, skin)
        root.add(dashCooldown)

        dashCooldown.isVisible = false // temporal

        stage.isDebugAll = true
        //endregion

        val interactor = GameScenarioCallbacks()
        scenarioHolder = ScenarioHolder(
            "$scenarioPath/$scenarioName",
            interactor,
            engine, registry,
            EntitySpawner(
                world, engine,
                NightsEdgeLoader(EarClippingTriangulator(), assetManager, world),
                rayHandler
            )
        )

        engine.addEntity(EntityOf(RayHandlerComponent(rayHandler)))
        engine.addEntity(EntityOf(CameraComponent(camera)))

        engine.apply {
            addSystem(WeaponSystem(controller))
            addSystem(LunaLookSystem(controller))
            addSystem(GroundFrictionSystem())
            addSystem(AiSystem(world))
            addSystem(RegistrySystem(registry))
            addSystem(ImpaleSystem(world))
            addSystem(CooldownsSystem())
            addSystem(AerialSystem())
            addSystem(LunaBodySystem(controller))
            addSystem(ForcesSystem())
            addSystem(WorldSystem(world))
            addSystem(CollisionSystem(world))
            addSystem(DamageSystem())
            addSystem(LunaVitalitySystem(interactor))
            addSystem(ScenarioCollisionSystem(scenarioHolder))
            addSystem(PositionSystem())
            addSystem(PositionalLightsSystem())
            addSystem(HurtboxFollowSystem())
            addSystem(InteractionSystem())
            addSystem(CameraSystem())
            addSystem(DisplaySystem(dashCooldown))
            addSystem(DebugRenderSystem(world, debugRenderer))
            addSystem(RenderSystem(assetManager))
            addSystem(DisposalSystem())
        }

        scenarioHolder.initialize(saveFile)
    }

    override fun render(delta: Float) {
        if (stateReady) {
            ClearScreen()
            scenarioHolder.update(delta)
            engine.update(delta)
            stage.act(delta)
            stage.draw()
        } else {
            ClearScreen(b = 0.3f)
            levelLoader.update()
        }
    }

    override fun dispose() {
        engine.removeAllEntities()
        engine.systems.forEach(engine::removeSystem)
        world.dispose()
        debugRenderer.dispose()
        Controllers.clearListeners()
        core.commandExecutor.currentEngine = null
        rayHandler.dispose()
    }

    inner class GameScenarioCallbacks : ScenarioCallbacks {

        override fun loadLevel(name: String) {
            levelLoader.loadLevel(name) { map ->
                val loadedLevel = Level(map)
                engine.addEntity(EntityOf(LevelComponent(loadedLevel)))
                loadedLevel.inflate(world, engine)
                stateReady = true
                scenarioHolder.levelReady()
                assetManager["sword_small.png", Texture::class.java]
            }
        }

        override fun gameOver(iconFile: String, title: String, description: String) {
            Gdx.app.postRunnable {
                core.setScreen(ScreenImage.GameOver(iconFile, title, description))
            }
        }

    }

}
