package ac.github.oa.internal.base.event.impl

import ac.github.oa.OriginAttribute
import ac.github.oa.api.event.entity.OriginCustomDamageEvent
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.core.attribute.Attribute
import ac.github.oa.internal.core.attribute.AttributeData
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common5.Coerce

class DamageMemory(
    val attacker: LivingEntity,
    val injured: LivingEntity,
    val event: OriginCustomDamageEvent,
    val attackAttributeData: AttributeData,
    val injuredAttributeData: AttributeData,
) : EventMemory {

    var arrow = attacker is Arrow

    val accumulatorPower =
        (event.damage / (attacker.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE)?.value
            ?: event.damage)).coerceAtLeast(0.0).coerceAtMost(1.0)

    companion object {

        val EMPTY_SOURCE = Source(-1, 0.0)
    }

    // 如果关闭原版属性 并且玩家是玩家 则启用
    var damage = if (!OriginAttribute.original) 0.0 else event.damage
    val labels = mutableMapOf<Any, Any>()

    fun setLabel(key: Any, value: Any): DamageMemory {
        labels[key] = value
        return this
    }

    fun addDamage(attribute: Attribute, value: Double): DamageMemory {
        return addDamage(attribute.toLocalName(), value)
    }

    val totalDamage: Double
        get() = (getDamageSources().sumOf { it.value } + damage)

    fun getDamage(key: Any): Double {
        val source = getDamageSources().firstOrNull { it.any == key } ?: EMPTY_SOURCE
        return Coerce.toDouble(source.value)
    }

    fun getDamageSources(): List<Source> {
        return labels.keys.filterIsInstance<Source>()
    }

    fun getDamageSource(any: Any) = getDamageSources().firstOrNull { it.any == any }

    fun addDamage(key: Any, value: Double): DamageMemory {
        setLabel(Source(key, value), value)
        return this
    }

    fun takeDamage(key: Any, value: Double): DamageMemory {
        addDamage(key, -value)
        return this
    }

    fun addDamage(value: Double): DamageMemory {
        damage += value
        return this
    }

    override fun toString(): String {
        return "DamageMemory(attacker=$attacker, injured=$injured, totalDamage=$totalDamage)"
    }


    class Source(val any: Any, var value: Double) {
        override fun toString(): String {
            return "Source(any=$any, value=$value)"
        }
    }

}
