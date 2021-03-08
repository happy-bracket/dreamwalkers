package ru.substancial.dreamwalkers.utilities

fun <T> Any?.cast(): T = this as T

fun <T> Any?.safeCast(): T? = this as? T
