package ac.github.oa.internal.attribute.impl.attack

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
 * 0 暴击几率
 * 1 暴击伤害
 * 2 爆伤抵抗
 */
class Crit : AttributeAdapter(3, AttributeType.ATTACK) {
    override fun defaultOption(config: BaseConfig) {
        config.select(this)
            .setStrings("暴击几率")
            .set("combat-power", 1)
            .superior()
            .select("crit-damage")
            .setStrings("暴击伤害")
            .set("default", 100.0)
            .set("combat-power", 1)
            .superior()
            .select("crit-damage-defense")
            .set("combat-power", 1)
            .setStrings("暴伤抵抗")
    }

    val defaultCritDamage: Double
        get() = baseConfig.select("crit-damage").any("default").asNumber().toDouble()

    override

    fun count(baseDoubles: Array<BaseDouble>): Long {
        return (baseDoubles[0].number() * baseConfig.select(this).any("combat-power").asNumber().toDouble() +
                baseDoubles[1].number() * baseConfig.select("crit-damage").any("combat-power").asNumber().toDouble() +
                baseDoubles[2].number() * baseConfig.select("crit-damage-defense").any("combat-power").asNumber()
            .toDouble()
                ).roundToLong()
    }

    override fun inject(entity: LivingEntity?, string: String, baseDoubles: Array<BaseDouble>) {
        baseDoubles[0].merge(baseConfig.analysis(this, string, ValueType.PERCENT))
        baseDoubles[1].merge(baseConfig.analysis("crit-damage", string, ValueType.ALL))
        baseDoubles[2].merge(baseConfig.analysis("crit-damage-defense", string, ValueType.NUMBER))
    }

    override fun format(entity: LivingEntity, s: String, baseDoubles: Array<BaseDouble>): Any {
        return if (s.equals("p", ignoreCase = true)) {
            baseDoubles[0].number()
        } else if (s.equals("defense", ignoreCase = true)) {
            baseDoubles[2].number()
        } else if (s.equals("damage", ignoreCase = true)) {
            baseDoubles[1].value(ValueType.NUMBER)
        } else {
            baseDoubles[1].value(ValueType.PERCENT)
        }
    }

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        if (eventMemory is DamageMemory) {
            val damageMemory: DamageMemory = eventMemory
            if (baseDoubles[0].random()) {
                var encapsulation: Double = baseDoubles[1].globalEval(0.0, defaultCritDamage, damageMemory.damage)
                encapsulation -= encapsulation * damageMemory.injuredAttributeData.find(Crit::class.java)[2]
                    .percent(ValueType.NUMBER)
                damageMemory.setLabel(Crit::class.java, true)
                    .setLabel("crit-damage", encapsulation)
                println("crit 1 = ${damageMemory.damage} 2 = $encapsulation")
                damageMemory.damage = encapsulation
            }
        }
    }
}