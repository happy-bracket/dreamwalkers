package ru.substancial.dreamwalkers.utilities

import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.CoerceLuaToJava

inline class LuaFunctionName(val name: String)

val Any?.lua: LuaValue
    get() = CoerceJavaToLua.coerce(this)

val LuaValue.java: Any?
    get() = CoerceLuaToJava.coerce(this, Any::class.java)
