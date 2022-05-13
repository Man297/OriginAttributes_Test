package ac.github.oa.internal.core.condition

import ac.github.oa.internal.core.equip.AdaptItem
import org.bukkit.entity.LivingEntity
import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
import taboolib.platform.util.isNotAir

object ConditionManager {

    val conditions = arrayListOf<ICondition>()

    @Awake(LifeCycle.ENABLE)
    fun loadCondition() {
        runningClasses.forEach {
            if (ICondition::class.java.isAssignableFrom(it)) {
                val any = it.getInstance()?.get() ?: return@forEach
                conditions += any as ICondition
                info("Loaded condition ${it.simpleName}")
            }
        }
    }


    fun pre(livingEntity: LivingEntity, adaptItem: AdaptItem): Boolean {
        val item = adaptItem.item
        return item.isNotAir() && item.hasItemMeta() && item.itemMeta!!.hasLore()
    }

    fun screen(livingEntity: LivingEntity, adaptItem: AdaptItem): Boolean {
        return conditions.all { it.post(livingEntity, adaptItem) }
    }
}