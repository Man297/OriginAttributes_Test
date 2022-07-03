package ac.github.oa.internal.core.condition

import ac.github.oa.api.ItemAPI
import ac.github.oa.internal.core.attribute.getNumber
import ac.github.oa.internal.core.attribute.equip.AdaptItem
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.platform.util.sendLang

object DurabilityCondition : ICondition {
    override fun post(livingEntity: LivingEntity, adaptItem: AdaptItem): Boolean {

        if (livingEntity is Player) {

            val item = adaptItem.item

            val list = options("durability").getStringList("keyword")
            val lore = item.itemMeta!!.lore!!
            if (any(lore, list)) {
                if (ItemAPI.getDurability(adaptItem.item) <= 1) {
                    livingEntity.sendLang("condition-durability-not-enough", item.itemMeta!!.displayName)
                    return false
                }
            }
        }
        return true
    }
}
