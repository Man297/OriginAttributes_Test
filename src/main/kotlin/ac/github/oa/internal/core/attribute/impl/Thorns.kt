package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.core.attribute.AbstractAttribute
import ac.github.oa.internal.core.attribute.Attribute
import ac.github.oa.internal.core.attribute.AttributeData
import ac.github.oa.internal.core.attribute.AttributeType
import taboolib.common.platform.function.submit

class Thorns : AbstractAttribute() {

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.DEFENSE)


    override fun onLoad() {
        submit {

        }
    }

    val thorns = object : Attribute.Entry() {
        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE
        override fun handler(memory: EventMemory, data: AttributeData.Data) {

        }

    }


}
