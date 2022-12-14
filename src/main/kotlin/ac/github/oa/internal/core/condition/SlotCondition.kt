package ac.github.oa.internal.core.condition

import ac.github.oa.internal.core.attribute.equip.AdaptItem
import ac.github.oa.internal.core.attribute.equip.SlotVariation
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.platform.util.sendLang

object SlotCondition : ICondition {
    override fun post(livingEntity: LivingEntity, adaptItem: AdaptItem): Boolean {


        if (livingEntity is Player) {

            val item = adaptItem.item

            val list = options("slot").getStringList("keyword")
            val lore = item.itemMeta!!.lore!!

            val slot = adaptItem.slot
            val simpleName = slot::class.simpleName
            val stringList = options("slot").getStringList("pattern.$simpleName")

            val defaultResult = slot !is SlotVariation

            if (any(lore, list)) {
                if (slot is SlotVariation) return slot.examine(livingEntity, adaptItem, stringList)


                val string = lore.first { list.any { s -> it.contains(s) } }
                if (!slot.screen(string, stringList)) {
                    livingEntity.sendLang("condition-slot-not-enough", item.itemMeta!!.displayName, stringList[0])
                    return false
                }
            } else {
                return defaultResult
            }
        }
        return true
    }
}