package ac.github.oa.internal.core.attribute

import ac.github.oa.internal.core.equip.AdaptItem
import taboolib.common.util.random
import java.math.BigDecimal

class AttributeData {

    val items = mutableListOf<AdaptItem>()

    var tables: Array<Array<Data>>
    var combatPower = 0.0

    fun getArrayData(index: Int): Array<Data> {
        return tables[index]
    }

    fun getData(attributeIndex: Int, entryIndex: Int): Data {
        return getArrayData(attributeIndex)[entryIndex]
    }

    init {
        val attributes = AttributeManager.usableAttributes
        this.tables = Array(attributes.size) {
            val iAttribute = attributes[it]!!
            Array(iAttribute.toEntrySize()) { entryIndex ->
                Data(iAttribute.getEntry(entryIndex).type.size)
            }
        }
    }

    fun merge(data: AttributeData) {

    }

    fun autoCombatPower() {

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
                Attribute.Type.RANGE -> random(array[0], array[1])
            }
        }

    }

}
