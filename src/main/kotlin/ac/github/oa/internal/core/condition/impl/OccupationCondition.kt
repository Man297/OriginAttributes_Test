package ac.github.oa.internal.core.condition.impl

import ac.github.oa.internal.core.condition.ICondition
import ac.github.oa.internal.core.equip.AdaptItem
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.platform.util.sendLang

object OccupationCondition : ICondition {
    override fun post(livingEntity: LivingEntity, adaptItem: AdaptItem): Boolean {

        if (livingEntity is Player) {

            val item = adaptItem.item
            val config = options("occupation")
            val list = config.getStringList("keyword")
            val lore = item.itemMeta!!.lore!!
            if (any(lore, list)) {
                val map = config.getStringList("pattern").map {
                    val split = it.split(" ")
                    Pair(split[0], split[1])
                }
                val data = map.firstOrNull { lore.any { s -> s.contains(it.first) } }
                if (data != null && !livingEntity.hasPermission(data.second)) {
                    livingEntity.sendLang("condition-occupation-not-enough", item.itemMeta!!.displayName, data.first)
                    return false
                }
            }

        }
        return true
    }
}
