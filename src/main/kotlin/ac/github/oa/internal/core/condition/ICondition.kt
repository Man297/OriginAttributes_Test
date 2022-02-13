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

interface ICondition {

    fun post(livingEntity: LivingEntity, adaptItem: AdaptItem): Boolean

    val options: ConfigurationSection
        get() = OriginAttribute.config.getConfigurationSection("options.condition")!!

    fun options(string: String): ConfigurationSection = options.getConfigurationSection(string)!!

    fun any(list: List<String>, keyword: List<String>) = list.any { keyword.any { s -> it.contains(s) } }

}