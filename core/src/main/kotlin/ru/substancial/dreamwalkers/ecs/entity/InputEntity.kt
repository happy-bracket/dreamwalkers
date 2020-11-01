package ru.substancial.dreamwalkers.ecs.entity

import com.badlogic.ashley.core.Entity
import ru.substancial.dreamwalkers.ecs.component.InputComponent

fun InputEntity(): Entity = Entity().add(InputComponent())