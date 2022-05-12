package ac.github.oa.internal.core.condition.impl

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.core.condition.ICondition
import ac.github.oa.internal.core.equip.AdaptItem
import ac.github.oa.util.TimeUtil
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.util.sendLang
import java.time.LocalDateTime
import java.util.*

object TimeCondition : ICondition {
    override fun post(livingEntity: LivingEntity, adaptItem: AdaptItem): Boolean {

        if (livingEntity is Player) {

            val item = adaptItem.item

            val list = options("time").getStringList("keyword")
            val lore = item.itemMeta!!.lore!!
            if (any(lore, list)) {
                var keyword = ""
                val string = lore.first {
                    list.any { s ->
                        if (it.contains(s)) {
                            keyword = s
                            true
                        } else false
                    }
                }

                val time = getTime(string, keyword)
                val date = OriginAttribute.simpleDateFormat.parse(time)
                if (Date().after(date)) {
                    livingEntity.sendLang("condition-time-not-enough", item.itemMeta!!.displayName, time)
                    return false
                }
            }
        }
        return true
    }


    private fun getTime(lore: String, keyword: String): String {
        var str: String = lore.replace(keyword, "").replace("§+[a-z0-9]".toRegex(), "")
        if (str.contains(": ") || str.contains("： ")) {
            str = str.replace("： ", ": ")
            str = str.replace(str.split(":").toTypedArray()[0] + ": ", "")
            return str
        }
        return str
    }

}
