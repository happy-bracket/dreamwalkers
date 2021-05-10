package ru.substancial.dreamwalkers.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import ru.substancial.dreamwalkers.controls.TheController
import ru.substancial.dreamwalkers.ecs.component.CastingBindingsComponent
import ru.substancial.dreamwalkers.ecs.extract
import ru.substancial.dreamwalkers.ecs.system.Symbol.*
import ru.substancial.dreamwalkers.ui.CastingBufferDisplay
import ru.substancial.dreamwalkers.utilities.RegisteringSystem

/* TODO
 * so, this thing revealed a lot of problems with the current state of things.
 * 1) physics World should be an Entity
 * 2) player's input should be an Entity
 */
class CastingSystem(
    private val controller: TheController,
    private val display: CastingBufferDisplay
) : RegisteringSystem() {

    private val buffer = ArrayList<Symbol>(4)

    private val bindingsContainer by optional(Family.all(CastingBindingsComponent::class.java).get())

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        controller.aListener = { process(A) }
        controller.bListener = { process(B) }
        controller.xListener = { process(X) }
        controller.yListener = { process(Y) }
        controller.r1Listener = { commit() }
    }

    private fun process(sym: Symbol) {
        if (buffer.size == 4) return
        buffer.add(sym)
        display.add(sym)
    }

    private fun commit() {
        bindingsContainer?.extract<CastingBindingsComponent>()?.bindings?.get(buffer)?.exec?.invoke(engine)
        buffer.clear()
        display.clear()
    }

}

sealed class Symbol {
    object A : Symbol()
    object B : Symbol()
    object X : Symbol()
    object Y : Symbol()
}
