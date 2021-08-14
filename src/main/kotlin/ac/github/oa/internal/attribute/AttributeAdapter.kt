package ac.github.oa.internal.attribute


import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import org.bukkit.entity.LivingEntity


abstract class AttributeAdapter(
    val length: Int,
    vararg attributeType: AttributeType
) : Comparable<AttributeAdapter> {

    open val name: String
        get() = this::class.simpleName.toString()

    lateinit var baseConfig: BaseConfig

    open val createFile: Boolean
        get() = true

    val attributeTypes: List<AttributeType> = listOf(*attributeType)

    var priority: Int = -1

    abstract fun inject(entity: LivingEntity?, string: String, baseDoubles: Array<BaseDouble>)

    open fun load() {}

    open fun enable() {}

    open fun disable() {}

    open fun reload() {}

    open fun defaultOption(config: BaseConfig) {}

    abstract fun format(entity: LivingEntity, s: String, baseDoubles: Array<BaseDouble>): Any?

    abstract fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>)

    open fun register() {

        AttributeManager.register(this)
    }

    override operator fun compareTo(sub: AttributeAdapter): Int {
        return priority.compareTo(sub.priority)
    }

    companion object {

        fun getNumber(lore: String): BaseDouble {
            return getNumber(lore, ValueType.NUMBER)
        }

        fun getNumber(lore: String, valueType: ValueType): BaseDouble {
            var str = lore.replace("§+[a-z0-9]".toRegex(), "")
                .replace("-[^0-9]".toRegex(), "")
                .replace("[^-0-9.%?]".toRegex(), "")
            val s = if (str.isEmpty() || str.replace("[^.]".toRegex(), "").length > 1) "0.0" else str
            val lastChar = s[str.length - 1]
            val baseDouble = BaseDouble()
            val percent = lastChar == '%'
            if (percent) {
                str = str.substring(0, str.length - 1)
            }
            val valueOf = java.lang.Double.valueOf(str)
            if (valueType === ValueType.NUMBER) {
                if (percent) {
                    baseDouble.put(ValueType.PERCENT, valueOf)
                } else {
                    baseDouble.put(ValueType.NUMBER, valueOf)
                }
            } else if (valueType === ValueType.PERCENT) {
                baseDouble[ValueType.NUMBER] = valueOf
            } else {
                baseDouble[if (percent) ValueType.PERCENT else ValueType.NUMBER] = valueOf
            }
            return baseDouble
        }
    }
}
