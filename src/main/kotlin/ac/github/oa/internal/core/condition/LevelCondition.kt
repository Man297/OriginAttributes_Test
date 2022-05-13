package ac.github.oa.internal.core.condition

import ac.github.oa.internal.core.attribute.getNumber
import ac.github.oa.internal.core.condition.ICondition
import ac.github.oa.internal.core.equip.AdaptItem
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common5.Coerce
import taboolib.platform.util.sendLang

object LevelCondition : ICondition {
    override fun post(livingEntity: LivingEntity, adaptItem: AdaptItem): Boolean {

        if (livingEntity is Player) {

            val item = adaptItem.item

            val list = options("level").getStringList("keyword")
            val lore = item.itemMeta!!.lore!!
            if (any(lore, list)) {
                val string = lore.first { list.any { s -> it.contains(s) } }
                val number = Coerce.toInteger(getNumber(string))
                if (livingEntity.level < number) {
                    livingEntity.sendLang("condition-level-not-enough", item.itemMeta!!.displayName, number)
                    return false
                }
            }
        }
        return true
    }
}
