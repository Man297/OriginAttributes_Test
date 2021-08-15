package ac.github.oa.internal.core.condition

import ac.github.oa.internal.core.equip.AdaptItem
import org.bukkit.entity.LivingEntity
import taboolib.common.LifeCycle
import taboolib.common.inject.Injector
import taboolib.common.platform.Awake
import java.util.function.Supplier

@Awake(LifeCycle.CONST)
object ConditionManager {
    val conditions = arrayListOf<ICondition>()
}

@Awake(LifeCycle.INIT)
object ConditionLoader : Injector.Classes {
    override val lifeCycle: LifeCycle
        get() = LifeCycle.INIT
    override val priority: Byte
        get() = 1

    override fun inject(clazz: Class<*>, instance: Supplier<*>) {

        if (ICondition::class.isInstance(instance)) {
            ConditionManager.conditions.add((instance as ICondition))
        }

    }

    override fun postInject(clazz: Class<*>, instance: Supplier<*>) {}

}

interface ICondition {

    fun post(livingEntity: LivingEntity, adaptItem: AdaptItem): Boolean

}