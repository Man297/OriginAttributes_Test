package ac.github.oa.internal.attribute.impl.attack

import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import org.bukkit.entity.LivingEntity

/**
 * 0 吸血几率
 * 1 吸血量
 */
class BloodSucking : AttributeAdapter(2) {

    override fun defaultOption(config: BaseConfig) {
        config.select(this)
            .setStrings("吸血几率")
            .superior()
            .select("blood-sucking-value")
            .setStrings("吸血量")
    }

    override fun inject(entity: LivingEntity?, string: String, baseDoubles: Array<BaseDouble>) {
        baseDoubles[0].merge(baseConfig.analysis(this, string, ValueType.PERCENT))
        baseDoubles[1].merge(baseConfig.analysis("blood-sucking-value", string, ValueType.ALL))
    }

    override fun format(entity: LivingEntity, s: String, baseDoubles: Array<BaseDouble>): Any {
        return baseDoubles[if (s.equals("p", ignoreCase = true)) 0 else 1].number()
    }

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        if (eventMemory is DamageMemory) {
            val damageMemory: DamageMemory = eventMemory
            if (baseDoubles[0].random()) {
                damageMemory.setLabel(Crit::class.java, true)
                    .addDamage("blood-sucking-value", baseDoubles[0].globalEval(damageMemory.damage))
            }
        }
    }
}