package ac.github.oa.internal.core.condition.impl

import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.core.condition.ICondition
import ac.github.oa.internal.core.equip.AdaptItem
import ac.github.oa.internal.core.equip.InventorySlot
import ac.github.oa.internal.core.equip.SlotVariation
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.util.sendLang

@Awake(LifeCycle.ENABLE)
class SlotCondition : ICondition {
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