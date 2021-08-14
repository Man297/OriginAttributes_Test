package ac.github.oa.internal.base.event.impl

import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.attribute.AttributeData
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.util.Strings
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent

class DamageMemory(
    val attacker: LivingEntity,
    val injured: LivingEntity,
    val event: EntityDamageByEntityEvent,
    val attackAttributeData: AttributeData,
    val injuredAttributeData: AttributeData
) : EventMemory {

    var arrow = event.damager is Arrow
    var damage = event.damage
    val labels = mutableMapOf<Any, Any>()

    fun setLabel(key: Any, value: Any): DamageMemory {
        labels[key] = value
        return this
    }

    fun addDamage(attributeAdapter: AttributeAdapter, value: Double): DamageMemory {
        return addDamage(Strings.parseLowerString(attributeAdapter::class.java.simpleName), value)
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