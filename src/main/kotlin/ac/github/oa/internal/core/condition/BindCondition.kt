package ac.github.oa.internal.core.condition

import ac.github.oa.internal.core.attribute.equip.AdaptItem
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.platform.util.sendLang

object BindCondition : ICondition {

    val prefix: String
        get() = options.getString("bind.prefix", "__null__")!!

    val suffix: String
        get() = options.getString("bind.suffix", "")!!

    val COLOR_REGEX = Regex("§+[a-z0-9]")

    fun check(string: String): String? {
        val afterString = string.replace(COLOR_REGEX, "")
        if (afterString.startsWith(prefix) && afterString.endsWith(suffix)) {
            return afterString.replace(prefix, "").replace(suffix, "").trim()
        }
        return null
    }

    override fun post(livingEntity: LivingEntity, adaptItem: AdaptItem): Boolean {

        var checkPlayer: String? = null

        if (livingEntity is Player) {
            val item = adaptItem.item
            val lore = item.itemMeta!!.lore!!
            lore.forEach {
                if (checkPlayer != null) return@forEach
                checkPlayer = check(it) ?: return@forEach
            }
        }

        return if (checkPlayer != null && checkPlayer != livingEntity.name) {
            livingEntity.sendLang("condition-bind-not-enough", adaptItem.item.itemMeta!!.displayName, checkPlayer!!)
            false
        } else true
    }
}