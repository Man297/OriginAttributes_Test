package ac.github.oa.internal.attribute.impl.defense

import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import org.bukkit.entity.LivingEntity

/**
 * 0 反弹概率
 * 1 反弹伤害
 */
class Rebound : AttributeAdapter(2) {
    override fun defaultOption(config: BaseConfig) {
        config.select(this).setStrings("反弹概率")
            .superior()
            .select("rebound-damage")
            .setStrings("反弹伤害")
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