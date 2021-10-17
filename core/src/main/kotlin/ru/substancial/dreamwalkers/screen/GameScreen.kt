package ru.substancial.dreamwalkers.screen

import box2dLight.RayHandler
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.math.EarClippingTriangulator
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.esotericsoftware.spine.AnimationState
import com.esotericsoftware.spine.AnimationStateData
import com.esotericsoftware.spine.Skeleton
import com.esotericsoftware.spine.SkeletonData
import com.esotericsoftware.spine.utils.SkeletonDataLoader
import ru.substancial.dreamwalkers.Assets
import ru.substancial.dreamwalkers.Core
import ru.substancial.dreamwalkers.controls.TheController
import ru.substancial.dreamwalkers.ecs.component.*
import ru.substancial.dreamwalkers.ecs.entity.EntitySpawner
import ru.substancial.dreamwalkers.ecs.other.AvailableInteractions
import ru.substancial.dreamwalkers.ecs.system.*
import ru.substancial.dreamwalkers.files.DreamwalkersAssetManager
import ru.substancial.dreamwalkers.files.NightsEdgeLoader
import ru.substancial.dreamwalkers.level.*
import ru.substancial.dreamwalkers.ui.DevCastingBufferDisplay
import ru.substancial.dreamwalkers.utilities.ClearScreen
import ru.substancial.dreamwalkers.utilities.EntityOf
import ru.substancial.dreamwalkers.utilities.IdentityRegistry
import ru.substancial.dreamwalkers.utilities.cast

class GameScreen(
    private val assetManager: DreamwalkersAssetManager,
    private val core: Core,
    scenarioPath: String,
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

    private val interactions: AvailableInteractions

    private val scenarioHolder: ScenarioHolder
    private val registry: IdentityRegistry = IdentityRegistry()

    private val engine = Engine()

    private val levelLoader = LevelLoader(scenarioPath, assetManager)
    private var stateReady = false

    init {
        assetManager.load(
            Assets.Luna.Skeleton, SkeletonData::class.java,
            SkeletonDataLoader.SkeletonDataParameter(Assets.Luna.Atlas)
        )

        Controllers.addListener(controller)
        core.commandExecutor.currentEngine = engine

        // region UI
        // region TopLeft
        val dashCooldown = ProgressBar(0f, 1f, 0.01f, true, skin)
        val dashContainer = Container(dashCooldown)
        dashContainer.size(64f)

        val healthBar = ProgressBar(0f, 1f, 0.01f, false, skin)
        val shieldsBar = ProgressBar(0f, 1f, 0.01f, false, skin)
        val manaBar = ProgressBar(0f, 1f, 0.01f, false, skin)
        val barsContainer = VerticalGroup()
        barsContainer.addActor(healthBar)
        barsContainer.addActor(shieldsBar)
        barsContainer.addActor(manaBar)
        barsContainer.width = 128f
        barsContainer.height = 64f

        val topLeftContainer = HorizontalGroup()
        topLeftContainer.addActor(dashContainer)
        topLeftContainer.addActor(barsContainer)
        topLeftContainer.align(Align.topLeft)
        // endregion

        // region BottomLeft
        val interactionsHint = Label("Interactions", skin)
        val interactionsContainer = Container(interactionsHint)
        interactionsContainer.size(64f).align(Align.bottomLeft)
        // endregion

        // region TopRight
        val castingBuffer = DevCastingBufferDisplay(skin)
        val castingBufferContainer = Container(castingBuffer)
        castingBufferContainer.width(128f).height(32f).align(Align.topRight)
        // endregion

        // region BottomRight
        val weaponHint = Label("Weapon", skin)
        val weaponHintContainer = Container(weaponHint)
        weaponHintContainer.size(64f).align(Align.bottomRight)
        // endregion

        val root = Stack(topLeftContainer, interactionsContainer, castingBufferContainer, weaponHintContainer)
        root.setFillParent(true)
        stage.addActor(root)
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

        interactions = AvailableInteractions(VerticalGroup(), scenarioHolder, skin)

        addEssentialEntities()

        engine.apply {
            addSystem(CastingSystem(controller, castingBuffer))
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
            addSystem(DeathSystem(interactor))
            addSystem(ScenarioCollisionSystem(scenarioHolder))
            addSystem(PositionSystem())
            addSystem(PositionalLightsSystem())
            addSystem(HurtboxFollowSystem())
            addSystem(InteractionSystem(world, interactions))
            addSystem(SkeletonSystem())
            addSystem(CameraSystem())
            addSystem(DisplaySystem(dashCooldown, healthBar, shieldsBar, manaBar))
            addSystem(DebugRenderSystem(world, debugRenderer))
            addSystem(RenderSystem(assetManager))
            addSystem(DisposalSystem())
        }

        scenarioHolder.initialize(saveFile)
    }

    override fun render(delta: Float) {
        if (stateReady) {
            ClearScreen()
            finishSetup()
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
        levelLoader.unloadLevel()
        engine.removeAllEntities()
        engine.systems.forEach(engine::removeSystem)
        world.dispose()
        debugRenderer.dispose()
        Controllers.clearListeners()
        core.commandExecutor.currentEngine = null
        rayHandler.dispose()
    }

    private fun finishSetup() {
        val lunaSkeletonData = assetManager[Assets.Luna.Skeleton, SkeletonData::class.java]
        val skeleton = Skeleton(lunaSkeletonData)
        val scaleX = 3.6f / skeleton.data.width
        val scaleY = 3.4f / skeleton.data.height
        //skeleton.setScale(scaleX, scaleY)
        val animationStateData = AnimationStateData(lunaSkeletonData)
        animationStateData.defaultMix = 0.2f
        val animationState = AnimationState(animationStateData)
        engine.getEntitiesFor(Family.all(LunaComponent::class.java).get()).first()
            .add(SkeletonComponent(skeleton, animationState, Vector2((scaleX * lunaSkeletonData.width) / 2, (scaleY * lunaSkeletonData.height) / 2)))
        animationState.setAnimation(0, "walk", true)
    }

    private fun addEssentialEntities() {
        engine.addEntity(EntityOf(RayHandlerComponent(rayHandler)))
        engine.addEntity(EntityOf(CameraComponent(camera)))
    }

    inner class GameScenarioCallbacks : ScenarioCallbacks {

        override fun loadLevel(name: String) {
            levelLoader.loadLevel(name) { map ->
                val loadedLevel = Level(map)
                engine.addEntity(EntityOf(LevelComponent(loadedLevel)))
                loadedLevel.inflate(world, engine)
                stateReady = true
                scenarioHolder.levelReady()
            }
        }

        override fun unloadLevel() {
            stateReady = false
            engine.removeAllEntities()
            addEssentialEntities()
            levelLoader.unloadLevel()
        }

        override fun gameOver(iconFile: String, title: String, description: String) {
            Gdx.app.postRunnable {
                core.setScreen(ScreenImage.GameOver(iconFile, title, description))
            }
        }

    }

}
