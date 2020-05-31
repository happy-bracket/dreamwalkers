package ru.substancial.dreamwalkers.ecs.component

object ButtonLayout {

    const val L = "L"
    const val L1 = "L1"
    const val L2 = "L2"
    const val R = "R"
    const val R1 = "R1"
    const val R2 = "R2"
    const val LX = "LX"
    const val LY = "LY"
    const val RX = "RX"
    const val RY = "RY"
    const val BCross = "BX"
    const val BTriangle = "BT"
    const val BCircle = "BC"
    const val BRect = "BR"

    val buttonToIndex = mutableMapOf(
            L to 0,
            L1 to 0,
            L2 to 0,
            R to 0,
            R1 to 0,
            R2 to 0,
            LX to 0,
            LY to 0,
            RX to 0,
            RY to 0,
            BCross to 0,
            BTriangle to 0,
            BCircle to 0,
            BRect to 0
    )

    operator fun get(index: String): Int {
        return buttonToIndex[index]!!
    }

}