package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import ac.github.oa.internal.core.attribute.AbstractAttribute
import ac.github.oa.internal.core.attribute.Attribute
import ac.github.oa.internal.core.attribute.AttributeData
import ac.github.oa.internal.core.attribute.AttributeType
import taboolib.common.platform.function.info
import taboolib.common5.Coerce

class Resistance : AbstractAttribute() {

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.DEFENSE)

    val ratio = object : Attribute.Entry() {
        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            if (memory.labels.containsKey("suck-blood")) {
                val double = Coerce.toDouble(memory.labels["suck-blood"])
                memory.labels["suck-blood"] = double - (double * (data.get(0) / 100))
            }
        }

    }

}
