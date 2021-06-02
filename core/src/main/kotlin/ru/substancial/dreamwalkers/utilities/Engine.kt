package ru.substancial.dreamwalkers.utilities

import com.badlogic.ashley.core.Engine

fun Engine.setProcessing(isProcessing: Boolean) {
    systems.forEach { it.setProcessing(isProcessing) }
}
