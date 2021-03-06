package ru.substancial.dreamwalkers.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.ai.btree.BehaviorTree
import ru.substancial.dreamwalkers.ai.AiEnvironment

class AiComponent(
        val behaviorTree: BehaviorTree<AiEnvironment>
) : Component