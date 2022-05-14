package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import ac.github.oa.internal.base.event.impl.UpdateMemory
import ac.github.oa.internal.core.attribute.AbstractAttribute
import ac.github.oa.internal.core.attribute.Attribute
import ac.github.oa.internal.core.attribute.AttributeData
import ac.github.oa.internal.core.attribute.AttributeType
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce

class MoveSpeed : AbstractAttribute() {

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.UPDATE)

    val ratio = object : Attribute.Entry() {

        val base: Double
            get() = root.getDouble("$name.base", 0.2)

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as UpdateMemory
            val result = base + base * (data.get(0) / 100)
            val entity = memory.livingEntity
            if (entity is Player) {
                if (entity.walkSpeed.toDouble() == result) return
                submit(async = false) {
                    entity.walkSpeed = result.toFloat()
                }
            }


        }

    }

}
