package ac.github.oa.internal.attribute.impl.update

import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.UpdateMemory
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class Health : SingleAttributeAdapter(AttributeType.UPDATE) {
    override fun defaultOption(config: BaseConfig) {
        super.defaultOption(config)
        config.select(this)["default"] = 40.0
    }


    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        if (eventMemory is UpdateMemory && eventMemory.livingEntity is Player) {
            val updateMemory: UpdateMemory = eventMemory
            val number: Double = baseDoubles[0].number() + baseConfig.select(this)
                .any("default")
                .asNumber().toDouble()
            val livingEntity: LivingEntity = updateMemory.livingEntity
            val attribute: AttributeInstance = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!
            if (livingEntity.health > number) {
                livingEntity.health = number
            }
            if (attribute.getBaseValue() != number) {
                attribute.setBaseValue(number)
            }
        }
    }

    override val strings: Array<String>
        get() = arrayOf("血量上限")
    override val type: ValueType
        get() = ValueType.NUMBER
}