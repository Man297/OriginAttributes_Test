package ac.github.oa.internal.core.attribute

import ac.github.oa.internal.base.event.EventMemory
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

    fun getPriority() : Int

    fun setPriority(index: Int)



    abstract class Entry {

        var index: Int = -1
        open val type = Type.SINGLE
        lateinit var name : String
        lateinit var node: Attribute
        open fun onEnable() {}

        abstract fun handler(memory: EventMemory, data: AttributeData.Data)

        open fun toValue(entity: LivingEntity, args: String, data: AttributeData.Data): Any? {

            return Any()
        }

    }

    enum class Type(val size: Int) {
        SINGLE(1), RANGE(2)
    }

}
