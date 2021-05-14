package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import ru.substancial.dreamwalkers.ecs.component.CameraComponent
import ru.substancial.dreamwalkers.ecs.component.RayHandlerComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

class LightsSystem : RegisteringSystem() {

    private val cameraEntity by singular(Family.all(CameraComponent::class.java).get())
    private val handlerEntity by singular(Family.all(RayHandlerComponent::class.java).get())

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        handlerEntity.extract<RayHandlerComponent>().handler.setAmbientLight(0f, 0f, 0f, 1f)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        val cam = cameraEntity.extract<CameraComponent>().camera
        val han = handlerEntity.extract<RayHandlerComponent>().handler
        han.setCombinedMatrix(cam)
        han.updateAndRender()
    }

}
