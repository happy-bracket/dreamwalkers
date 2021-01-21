package ru.substancial.dreamwalkers.ai.trees

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.branch.DynamicGuardSelector
import ru.substancial.dreamwalkers.ai.AiEnvironment
import ru.substancial.dreamwalkers.ai.behaviors.ChaseLuna
import ru.substancial.dreamwalkers.ai.behaviors.SeesLuna
import ru.substancial.dreamwalkers.ai.behaviors.StayAimlessly

fun PatrollingDummyTree(): BehaviorTree<AiEnvironment> {

    val chase = ChaseLuna()
    chase.guard = SeesLuna()

    val sel = DynamicGuardSelector(
            ChaseLuna().apply { guard = SeesLuna() },
            StayAimlessly()
    )

    return BehaviorTree(sel)
}