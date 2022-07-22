package ac.github.oa.internal.core.attribute

import ac.github.oa.api.compat.OriginPlaceholder
import ac.github.oa.internal.base.event.EventMemory
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.LivingEntity

interface Attribute {

    fun onLoad()

    fun onReload()

    fun onDisable()

    fun toName(): String

    fun toLocalName(): String

    fun getEntry(index: Int): Entry

    fun getEntry(name: String): Entry

    fun toEntrySize(): Int

    fun getPriority(): Int

    fun setPriority(index: Int)

    fun toRoot(): taboolib.library.configuration.ConfigurationSection


    abstract class Entry {

        var index: Int = -1

        open val type = Type.SINGLE

        open lateinit var name: String

        open lateinit var node: Attribute

        open val combatPower: Double
            get() = node.toRoot().getDouble("${name}.combat-power", 0.0)

        open fun onEnable() {}

        abstract fun handler(memory: EventMemory, data: AttributeData.Data)

        open fun toValue(entity: LivingEntity, args: String, data: AttributeData.Data): Any? {
            if (type == Type.SINGLE) {
                return data.get(0)
            }
            if (type == Type.RANGE) {
                return when (args) {
                    "max" -> data.get(1)
                    "min" -> data.get(0)
                    "random" -> data.random()
                    else -> "${OriginPlaceholder.df2.format(data.get(0))} - ${OriginPlaceholder.df2.format(data.get(1))}"
                }
            }
            return "N/O"
        }


        @Suppress("UNCHECKED_CAST")
        open fun getCorrectRules(): List<List<Double>> {
            val path = "${this.name}.correct"
            val root = getRoot()
            return root.getMapList(path) as List<List<Double>>
        }

        open fun getKeywords(): List<String> {
            return getRoot().getStringList("${this.name}.keywords")
        }

    }

    enum class Type(val size: Int) {
        SINGLE(1), RANGE(2)
    }

}
