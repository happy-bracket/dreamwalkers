package ru.substancial.dreamwalkers.utilities

inline fun <T> T?.complement(with: () -> T): T =
        this ?: with()
