package ac.github.oa.internal.attribute.impl.defense

import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import org.bukkit.entity.LivingEntity
import kotlin.math.roundToLong

/**
 * 0 反弹概率
 * 1 反弹伤害
 */
class Rebound : AttributeAdapter(2, AttributeType.DEFENSE) {
    override fun defaultOption(config: BaseConfig) {
        config.select(this)
            .setStrings("反弹概率")
            .set("combat-power", 1)
            .superior()
            .select("rebound-damage")
            .setStrings("反弹伤害")
            .set("combat-power", 1)
    }


    override fun count(baseDoubles: Array<BaseDouble>): Long {
        return (baseDoubles[0].number() * baseConfig.select(this).any("combat-power").asNumber().toDouble() +
                baseDoubles[1].number() * baseConfig.select("rebound-damage").any("combat-power").asNumber()
            .toDouble()).roundToLong()
    }

    override fun inject(entity: LivingEntity?, string: String, baseDoubles: Array<BaseDouble>) {
        baseDoubles[0].merge(baseConfig.analysis(this, string, ValueType.PERCENT))
        baseDoubles[1].merge(baseConfig.analysis("rebound-damage", string, ValueType.PERCENT))
    }

    override fun format(entity: LivingEntity, s: String, baseDoubles: Array<BaseDouble>): Any {
        return if (s.equals("damage", ignoreCase = true)) baseDoubles[1].number() else baseDoubles[0].number()
    }

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        if (eventMemory is DamageMemory) {
            val damageMemory: DamageMemory = eventMemory
            if (baseDoubles[0].random()) {
                damageMemory.setLabel("rebound", true)
                val encapsulation: Double = baseDoubles[1].eval(damageMemory.damage)
                damageMemory.attacker.damage(encapsulation, damageMemory.injured)
            }
        }
    }
}