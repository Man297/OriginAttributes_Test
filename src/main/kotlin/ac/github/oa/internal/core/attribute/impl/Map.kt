package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.api.event.entity.EntityGetterDataEvent
import ac.github.oa.api.event.plugin.AttributeMapRenderEvent
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.core.attribute.*
import ac.github.oa.util.ArrayUtils
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info

class Map : AbstractAttribute() {

    companion object {

        const val NONE = "none"

        val mapInstance: Map
            get() = AttributeManager.getAttribute("Map") as Map

        @SubscribeEvent
        fun e(e: EntityGetterDataEvent) {
            val data = e.attributeData
            val entries = mutableListOf<Attribute.Entry>()
            if (e.livingEntity is Player) {
                entries += mapInstance
                    .entries
                    .filterIsInstance<DefaultImpl>()
                    .filter { it.permission == NONE || e.livingEntity.hasPermission(it.permission) }
            } else {
                entries += mapInstance.entries
            }

            entries.forEach {
                it as DefaultImpl
                val entryData = data.getData(mapInstance.index, it.index)
                val event =
                    AttributeMapRenderEvent(e.livingEntity, it.attributeList.toMutableList(), it)
                event.call()
                val readList = ArrayUtils.read(e.livingEntity, e.attributeData, event.list, entryData.get(it))
                val resultData = OriginAttributeAPI.loadList(e.livingEntity, readList)
                e.attributeData.merge(resultData)
            }
        }

    }


    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.UPDATE)

    override fun onLoad() {
        super.onLoad()
        root.getKeys(false).forEach {
            val defaultImpl = DefaultImpl()
            defaultImpl.node = this
            defaultImpl.name = it
            defaultImpl.index = entries.size
            entries += defaultImpl
        }
        entries.forEach { it.onEnable() }
    }

    open class DefaultImpl : Attribute.Entry() {

        open val attributeList: List<String>
            get() = node.toRoot().getStringList("${name}.attributes")

        open val permission: String
            get() = node.toRoot().getString("${name}.permission", NONE)!!

        open override fun handler(memory: EventMemory, data: AttributeData.Data) {}

    }

}
