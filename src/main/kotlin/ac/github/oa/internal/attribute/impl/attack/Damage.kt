package ac.github.oa.internal.attribute.impl.attack

import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.attribute.AttributeType

import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.util.random
import kotlin.math.roundToLong

/**
 * 0 攻击伤害 min
 * 1 攻击伤害 max
 * 2 对玩家增伤
 * 3 对怪物增伤
 */
class Damage : AttributeAdapter(4, AttributeType.ATTACK) {

    override fun defaultOption(config: BaseConfig) {
        config.select(this)
            .setStrings("攻击力")
            .set("combat-power",1)
            .superior()
            .select("damage-pvp")
            .setStrings("对玩家增伤")
            .set("combat-power",1)
            .superior()
            .select("damage-pve")
            .setStrings("对怪物增伤")
            .set("combat-power",1)
    }


    override fun count(baseDoubles: Array<BaseDouble>): Long {
        return (baseDoubles[0].number() * baseConfig.select(this).any("combat-power").asNumber().toDouble() +
                baseDoubles[1].number() * baseConfig.select("damage-pvp").any("combat-power").asNumber().toDouble() +
                baseDoubles[2].number() * baseConfig.select("damage-pve").any("combat-power").asNumber().toDouble()
                ).roundToLong()
    }

    override fun inject(entity: LivingEntity?, string: String, baseDoubles: Array<BaseDouble>) {

        if (string.contains("-")) {

            val list = baseConfig.select(this).any("strings").asStringList()
            if (list.any { string.contains(it) }) {
                val split = string.split("-")
                baseDoubles[0].merge(baseConfig.analysis(this, split[0], ValueType.NUMBER, true))
                baseDoubles[1].merge(baseConfig.analysis(this, split[1], ValueType.NUMBER, true))
            }

        } else {
            baseDoubles[0].merge(baseConfig.analysis(this, string, ValueType.NUMBER))
            baseDoubles[1].merge(baseConfig.analysis(this, string, ValueType.NUMBER))
        }

        baseDoubles[2].merge(baseConfig.analysis("damage-pvp", string, ValueType.PERCENT))
        baseDoubles[3].merge(baseConfig.analysis("damage-pve", string, ValueType.PERCENT))
    }

    override fun format(entity: LivingEntity, s: String, baseDoubles: Array<BaseDouble>): Any {
        return when (s) {
            "min" -> baseDoubles[0].number()
            "max" -> baseDoubles[1].number()
            "pvp" -> baseDoubles[2].number()
            "pve" -> baseDoubles[3].number()
            else -> "${baseDoubles[0].number()} - ${baseDoubles[1].number()}"
        }
    }

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        if (eventMemory is DamageMemory) {
            val damageMemory: DamageMemory = eventMemory
            damageMemory.addDamage("damage", random(baseDoubles[0].number(), baseDoubles[1].number()))
            val injured: LivingEntity = damageMemory.injured
            if (injured is Player) {
                damageMemory.addDamage("damage-pvp", baseDoubles[2].eval(damageMemory.damage))
            } else {
                damageMemory.addDamage("damage-pve", baseDoubles[3].eval(damageMemory.damage))
            }
        }
    }
}