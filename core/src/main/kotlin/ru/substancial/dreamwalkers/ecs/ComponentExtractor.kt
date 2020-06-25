package ru.substancial.dreamwalkers.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import ru.substancial.dreamwalkers.ecs.component.AerialComponent
import ru.substancial.dreamwalkers.ecs.component.BodyComponent
import ru.substancial.dreamwalkers.ecs.component.LunaComponent

object ComponentExtractor {

    val Body = ComponentMapper.getFor(BodyComponent::class.java)
    val Aerial = ComponentMapper.getFor(AerialComponent::class.java)
    val Luna = ComponentMapper.getFor(LunaComponent::class.java)

}

operator fun <T : Component> Entity.get(mapper: ComponentMapper<T>): T =
        mapper.get(this)