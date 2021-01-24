package ru.substancial.dreamwalkers.utilities

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper

class ComponentMapperFactoryForLua {

    fun getFor(componentClass: Class<Component>): ComponentMapper<Component> {
        return ComponentMapper.getFor(componentClass)
    }

}