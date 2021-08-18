package ac.github.oa.internal.core.condition.impl

import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.core.condition.ICondition
import ac.github.oa.internal.core.equip.AdaptItem
import ac.github.oa.internal.core.equip.InventorySlot
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

            val list = options("level").getStringList("keyword")
            val lore = item.itemMeta!!.lore!!

            val slot = adaptItem.slot
            val simpleName = slot::class.simpleName
            val stringList = options("slot").getStringList(simpleName)
            if (any(lore, list)) {
                val string = lore.first { list.any { s -> it.contains(s) } }
                if (!slot.screen(string, stringList)) {
                    livingEntity.sendLang("condition-slot-not-enough", item.itemMeta!!.displayName, stringList[0])
                    return false
                }
            }
        }
        return true
    }
}