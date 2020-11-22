package ru.substancial.dreamwalkers.ai.trees

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.branch.DynamicGuardSelector
import com.badlogic.gdx.ai.btree.decorator.Invert
import ru.substancial.dreamwalkers.ai.AiPair
import ru.substancial.dreamwalkers.ai.behaviors.ChaseLuna
import ru.substancial.dreamwalkers.ai.behaviors.SeesLuna
import ru.substancial.dreamwalkers.ai.behaviors.StayAimlessly

fun PatrollingDummyTree(): BehaviorTree<AiPair> {

    val chase = ChaseLuna()
    chase.guard = SeesLuna()

    val sel = DynamicGuardSelector(
            ChaseLuna().apply { guard = SeesLuna() },
            StayAimlessly()
    )

    return BehaviorTree(sel)
}