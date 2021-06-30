package ru.substancial.dreamwalkers.ecs.other

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import ru.substancial.dreamwalkers.ecs.component.Interaction
import ru.substancial.dreamwalkers.level.ScenarioHolder

class AvailableInteractions(
    private val interactionsContainer: WidgetGroup,
    private val holder: ScenarioHolder,
    private val skin: Skin
) {

    private val storage = HashMap<String, Interaction>()

    fun add(interaction: Interaction) {
        storage[interaction.id] = interaction
        visualize()
    }

    fun remove(id: String) {
        storage.remove(id)
        visualize()
    }

    private fun visualize() {
        interactionsContainer.clear()
        storage.values.forEach {
            interactionsContainer.addActor(visualize(it))
        }
    }

    private fun visualize(interaction: Interaction): Actor {
        val widget = TextButton(interaction.prompt, skin)
        widget.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                holder.call(interaction.functionName)
            }
        })
        return widget
    }

}
