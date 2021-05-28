package ru.substancial.dreamwalkers.utilities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.math.Vector2
import ru.substancial.dreamwalkers.ecs.component.IdentityComponent
import ru.substancial.dreamwalkers.ecs.component.PositionComponent

val MapObject.x: Float
    get() = when (this) {
        is RectangleMapObject -> rectangle.x
        is PolygonMapObject -> polygon.x
        is TextureMapObject -> x
        else -> throw IllegalArgumentException("temporal f off")
    }

val MapObject.y: Float
    get() = when (this) {
        is RectangleMapObject -> rectangle.y
        is PolygonMapObject -> polygon.y
        is TextureMapObject -> y
        else -> throw IllegalArgumentException("temporal f off")
    }

fun MapObject.extractPositionInto(entity: Entity): PositionComponent {
    val cmp = PositionComponent(Vector2(x, y))
    entity.add(cmp)
    return cmp
}

fun MapObject.extractIdentityInto(entity: Entity) {
    val id = properties["identity"] as? String?
    if (id != null)
        entity.add(IdentityComponent(id))
}
