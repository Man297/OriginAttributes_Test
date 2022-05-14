package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.core.attribute.*
import org.bukkit.event.player.PlayerExpChangeEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.sendActionBar

class ExpAddon : AbstractAttribute() {

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.OTHER)

    companion object {

        val expAddonInstance: ExpAddon
            get() = AttributeManager.getAttribute("ExpAddon") as ExpAddon

        @SubscribeEvent
        fun e(e: PlayerExpChangeEvent) {
            val amount = e.amount
            val attributeData = OriginAttributeAPI.getAttributeData(e.player)
            val value = attributeData
                .getData(expAddonInstance.index, expAddonInstance.addon.index)
                .get(expAddonInstance.addon.index)
            val addon = (amount * (value / 100))
            e.amount += addon.toInt()
            if (expAddonInstance.addon.actionBar != null) {
                e.player.sendActionBar(
                    expAddonInstance.addon.actionBar!!.replace("{0}", amount.toString()).replace("{1}", addon.toString())
                )
            }
        }

    }

    val addon = DefaultImpl()

    class DefaultImpl : Attribute.Entry() {

        val actionBar: String?
            get() = node.toRoot().getString("$name.action-bar")

        override fun handler(memory: EventMemory, data: AttributeData.Data) {}
    }
}
