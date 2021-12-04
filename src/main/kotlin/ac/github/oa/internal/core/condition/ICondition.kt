package ac.github.oa.internal.core.condition

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.core.equip.AdaptItem
import org.bukkit.entity.LivingEntity
import taboolib.common.LifeCycle
import taboolib.common.inject.Injector
import taboolib.common.platform.Awake
import taboolib.library.configuration.ConfigurationSection
import taboolib.platform.util.isNotAir
import java.util.function.Supplier

@Awake(LifeCycle.INIT)
object ConditionManager : Injector.Classes {
    val conditions = arrayListOf<ICondition>()


    fun pre(livingEntity: LivingEntity, adaptItem: AdaptItem): Boolean {
        val item = adaptItem.item
        return item.isNotAir() && item.hasItemMeta() && item.itemMeta!!.hasLore()
    }

    fun screen(livingEntity: LivingEntity, adaptItem: AdaptItem): Boolean {
        return conditions.all { it.post(livingEntity, adaptItem) }
    }

    override val lifeCycle: LifeCycle
        get() = LifeCycle.LOAD
    override val priority: Byte
        get() = 1

    override fun inject(clazz: Class<*>, instance: Supplier<*>) {
        val any = instance.get()
        if (ICondition::class.isInstance(any)) {
            conditions.add((any as ICondition))
        }
    }

    override fun postInject(clazz: Class<*>, instance: Supplier<*>) {}

}

interface ICondition {

    fun post(livingEntity: LivingEntity, adaptItem: AdaptItem): Boolean


    val options: ConfigurationSection
        get() = OriginAttribute.config.getConfigurationSection("options.condition")!!

    fun options(string: String): ConfigurationSection = options.getConfigurationSection(string)!!

    fun any(list: List<String>, keyword: List<String>) = list.any { keyword.any { s -> it.contains(s) } }

}