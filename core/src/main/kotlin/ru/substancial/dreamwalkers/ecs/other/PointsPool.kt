package ru.substancial.dreamwalkers.ecs.other

class PointsPool(
    val capacity: Float,
    private var _points: Float
) {

    val points: Float
        get() = _points

    fun decreaseBy(amount: Float) {
        _points -= amount
        _points = _points.coerceAtLeast(0f)
    }

    fun set(amount: Float) {
        _points = amount.coerceIn(0f, capacity)
    }

    fun replenish(amount: Float) {
        _points += amount
        _points = _points.coerceAtMost(capacity)
    }

    fun isFull() = _points == capacity

    companion object {

        fun Full(capacity: Float): PointsPool = PointsPool(capacity, capacity)

    }

}
