package ac.github.oa.internal.core.condition

import ac.github.oa.internal.core.attribute.getNumber
import ac.github.oa.internal.core.attribute.equip.AdaptItem
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.platform.util.sendLang
import java.util.function.Function

object LevelCondition : ICondition {

    var check: Function<Player, Int> = Function { it.level }

    override fun post(livingEntity: LivingEntity, adaptItem: AdaptItem): Boolean {

        if (livingEntity is Player) {

            val item = adaptItem.item

            val list = options("level").getStringList("keyword")
            val lore = item.itemMeta!!.lore!!
            if (any(lore, list)) {
                val string = lore.first { list.any { s -> it.contains(s) } }
                val number = Coerce.toInteger(getNumber(string))
                if (check.apply(livingEntity) < number) {
                    livingEntity.sendLang("condition-level-not-enough", item.itemMeta!!.displayName, number)
                    return false
                }
            }
        }
        return true
    }
}
