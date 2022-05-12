package ac.github.oa.internal.base.event.impl

import ac.github.oa.OriginAttribute
import ac.github.oa.api.event.entity.OriginCustomDamageEvent
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.core.attribute.Attribute
import ac.github.oa.internal.core.attribute.AttributeData
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class DamageMemory(
    val attacker: LivingEntity,
    val injured: LivingEntity,
    val event: OriginCustomDamageEvent,
    val attackAttributeData: AttributeData,
    val injuredAttributeData: AttributeData
) : EventMemory {

    var arrow = attacker is Arrow

    // 如果关闭原版属性 并且玩家是玩家 则启用
    var damage = if (!OriginAttribute.original && attacker is Player) 0.0 else event.damage
    val labels = mutableMapOf<Any, Any>()

    fun setLabel(key: Any, value: Any): DamageMemory {
        labels[key] = value
        return this
    }

    fun addDamage(attribute: Attribute, value: Double): DamageMemory {
        return addDamage(attribute.toLocalName(), value)
    }

    fun addDamage(key: Any, value: Double): DamageMemory {
        setLabel(key, value).damage += value
        return this
    }

    fun addDamage(value: Double): DamageMemory {
        damage += value
        return this
    }

}
