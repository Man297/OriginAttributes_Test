package ac.github.oa.internal.attribute.impl.attack

import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.Awake

@Awake
class DirectionDamage : AttributeAdapter(2, AttributeType.ATTACK) {

    override fun defaultOption(config: BaseConfig) {
        config.select("just-damage")
            .setStrings(listOf("正向攻击"))
            .setCombatPower(1.0)
            .superior()
            .select("dorsum-damage")
            .setStrings(listOf("背向攻击"))
            .setCombatPower(1.0)
            .superior()
    }

    override fun inject(entity: LivingEntity?, string: String, baseDoubles: Array<BaseDouble>) {
        baseDoubles[0].merge(baseConfig.analysis("just-damage", string, ValueType.ALL))
        baseDoubles[1].merge(baseConfig.analysis("dorsum-damage", string, ValueType.ALL))
    }

    override fun format(entity: LivingEntity, s: String, baseDoubles: Array<BaseDouble>): Any? {
        return when (s) {
            "just" -> baseDoubles[0].value(ValueType.NUMBER)
            "just:p" -> baseDoubles[0].value(ValueType.PERCENT)
            "dorsum" -> baseDoubles[1].value(ValueType.NUMBER)
            "dorsum:p" -> baseDoubles[1].value(ValueType.PERCENT)
            else -> null
        }
    }

    fun isDorsum(damager: LivingEntity, entity: LivingEntity): Boolean {
        val v1 = damager.location.direction
        val v2 = entity.location.direction
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z <= 0
    }

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        eventMemory as DamageMemory
        val number: Double = baseDoubles[if (isDorsum(
                eventMemory.attacker,
                eventMemory.injured
            )
        ) 0 else 1].globalEval(eventMemory.damage)
        eventMemory.addDamage(this, number)
    }


}