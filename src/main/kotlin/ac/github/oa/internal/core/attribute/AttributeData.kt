package ac.github.oa.internal.core.attribute

import ac.github.oa.internal.core.attribute.equip.AdaptItem
import taboolib.common.util.random

open class AttributeData {

    val items = mutableListOf<AdaptItem>()

    var tables: Array<Array<Data>>
    var combatPower = 0.0

    open val isValid: Boolean
        get() = true

    fun getArrayData(index: Int): Array<Data> {
        return tables[index]
    }

    fun getData(attributeIndex: Int, entryIndex: Int): Data {
        return getArrayData(attributeIndex)[entryIndex]
    }

    init {
        val attributes = AttributeManager.usableAttributes
        this.tables = Array(attributes.size) {
            val iAttribute = attributes[it] ?: error("error $it")
            Array(iAttribute.toEntrySize()) { entryIndex ->
                Data(iAttribute.getEntry(entryIndex).type.size)
            }
        }
    }

    fun merge(target: AttributeData) {
        this.tables.forEachIndexed { index, arrayOfDatas ->
            arrayOfDatas.forEachIndexed { dataIndex, data ->
                data.merge(target.tables[index][dataIndex])
            }
        }
    }

    fun autoCombatPower() {
        var count = 0.0
        AttributeManager.usableAttributes.values.forEach {
            it.toEntities().forEach { entry ->
                count += when (entry.type) {
                    Attribute.Type.SINGLE -> this.getData(it.getPriority(), entry.index).get(entry)
                    Attribute.Type.RANGE -> {
                        val data = this.getData(it.getPriority(), entry.index)
                        (data.get(0) + data.get(1)) / 2
                    }
                }
            }
        }
        this.combatPower = count
    }

    override fun toString(): String {
        return "AttributeData(items=$items, tables=${tables.contentToString()}, combatPower=$combatPower)"
    }

    class Data(length: Int) {

        val array = Array(length) { 0.0 }

        fun set(index: Int, value: Double) {
            array[index] = value
        }

        fun add(index: Int, value: Double) {
            array[index] += value
        }

        fun get(entry: Attribute.Entry?): Double {
            return get(entry?.type ?: Attribute.Type.SINGLE)
        }

        fun get(type: Attribute.Type = Attribute.Type.SINGLE): Double {
            return when (type) {
                Attribute.Type.SINGLE -> array[0]
                Attribute.Type.RANGE -> this.random()
            }
        }

        fun random(): Double {
            return when (array.size) {
                1 -> {
                    random(0.0, array[0])
                }
                2 -> {
                    random(array[0], array[1])
                }
                else -> -1.0
            }
        }

        fun get(index: Int): Double {
            return array[index]
        }

        fun merge(data: Data) {
            this.array.forEachIndexed { index, _ ->
                add(index, data.get(index))
            }
        }

        override fun toString(): String {
            return "Data(array=${array.contentToString()})"
        }

    }

}
