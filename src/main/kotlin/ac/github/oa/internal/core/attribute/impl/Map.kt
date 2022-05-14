package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.api.event.entity.EntityGetterDataEvent
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.core.attribute.*
import ac.github.oa.util.ArrayUtils
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info

class Map : AbstractAttribute() {

    companion object {

        val mapInstance: Map
            get() = AttributeManager.getAttribute("Map") as Map

        @SubscribeEvent
        fun e(e: EntityGetterDataEvent) {
            val data = e.attributeData
            mapInstance.entries.forEach {
                it as DefaultImpl
                val entryData = data.getData(mapInstance.index, it.index)
                val readList = ArrayUtils.read(it.attributeList, entryData.get(it))
                info("${it.index } read $readList")
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

    class DefaultImpl : Attribute.Entry() {

        val attributeList: List<String>
            get() = node.toRoot().getStringList("${name}.attributes")

        override fun handler(memory: EventMemory, data: AttributeData.Data) {}

    }

}
