package ac.github.oa.internal.core.condition.impl

import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.core.condition.ICondition
import ac.github.oa.internal.core.equip.AdaptItem
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.util.sendLang

@Awake(LifeCycle.ENABLE)
class LevelCondition : ICondition {
    override fun post(livingEntity: LivingEntity, adaptItem: AdaptItem): Boolean {

        if (livingEntity is Player) {

            val item = adaptItem.item

            val list = options("level").getStringList("keyword")
            val lore = item.itemMeta!!.lore!!
            if (any(lore, list)) {
                val string = lore.first { list.any { s -> it.contains(s) } }
                val number = AttributeAdapter.getNumber(string).number()
                if (livingEntity.level < number) {
                    livingEntity.sendLang("condition-level-not-enough", item.itemMeta!!.displayName, number)
                    return false
                }
            }
        }
        return true
    }
}