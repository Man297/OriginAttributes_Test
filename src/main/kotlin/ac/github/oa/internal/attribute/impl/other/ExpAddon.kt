package ac.github.oa.internal.attribute.impl.other

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.attribute.AttributeData
import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import org.bukkit.event.player.PlayerExpChangeEvent
import taboolib.common.platform.event.SubscribeEvent

class ExpAddon : SingleAttributeAdapter(AttributeType.OTHER) {
    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {}

    @SubscribeEvent
    fun e(e: PlayerExpChangeEvent) {
        val amount: Int = e.amount
        val attributeData: AttributeData = OriginAttributeAPI.getAttributeData(e.player)
        val baseDoubles: Array<BaseDouble> = attributeData.find(ExpAddon::class.java)
        e.amount = (amount + baseDoubles[0].globalEval(e.amount.toDouble())).toInt()
    }

    override val strings: Array<String>
        get() = arrayOf("经验加成")
    override val type: ValueType
        get() = ValueType.ALL
}