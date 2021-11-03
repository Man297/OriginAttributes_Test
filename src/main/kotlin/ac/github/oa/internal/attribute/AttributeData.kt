package ac.github.oa.internal.attribute

import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.DataPair
import ac.github.oa.util.ArrayUtils
import org.bukkit.inventory.EntityEquipment
import taboolib.common.platform.function.info
import java.util.stream.Collectors
import java.util.stream.IntStream

class AttributeData {

    var entityEquipment: EntityEquipment? = null

    var combatPower: Long = 0


    fun autoCombatPower() {
        combatPower = 0
        AttributeManager.attributes.forEach {
            val arrayOfBaseDoubles = this.find(it)
            combatPower += it.count(arrayOfBaseDoubles)
        }
    }

    private val doubles: Array<Array<BaseDouble>> =
        Array(AttributeManager.attributes.size) { create(AttributeManager.attributes[it]) }


    fun filter(type: AttributeType): MutableList<DataPair<AttributeAdapter, Array<BaseDouble>>> {
        return AttributeManager.attributes
            .stream()
            .filter { attributeAdapter -> attributeAdapter.attributeTypes.contains(type) }
            .map { attributeAdapter ->
                val doubles = this.find(attributeAdapter)
                DataPair(attributeAdapter, doubles)
            }
            .collect(Collectors.toList())
    }

    fun insert(aClass: Class<*>) {
        AttributeManager.attributes
            .stream()
            .filter { attributeAdapter -> attributeAdapter::class.java == aClass }
            .findFirst()
            .ifPresent { attributeAdapter -> insert(attributeAdapter, create(attributeAdapter)) }
    }

    fun insert(attributeAdapter: AttributeAdapter, doubles: Array<BaseDouble>) {
        this.doubles[attributeAdapter.priority] = doubles
    }

    fun merge(data: AttributeData) {
        for (i in data.doubles.indices) {
            val baseDoubles = data.doubles[i]
            ArrayUtils.merge(doubles[i], baseDoubles)
        }
    }

    fun find(name: String): Array<BaseDouble> {
        return AttributeManager.attributes.stream()
            .filter { attributeAdapter -> attributeAdapter.name == name }
            .findFirst()
            .map(this::find).orElse(null)
    }

    fun find(c: Class<*>): Array<BaseDouble> {
        val adapter = AttributeManager
            .attributes.first { attributeAdapter -> attributeAdapter::class.java == c }
        return find(adapter.name)
    }

    fun find(attributeAdapter: AttributeAdapter): Array<BaseDouble> {
        return doubles[attributeAdapter.priority]
    }

    companion object {
        fun create(attributeAdapter: AttributeAdapter): Array<BaseDouble> {
            return Array(attributeAdapter.length) { BaseDouble() }
        }
    }

} 