package ac.github.oa.internal.core.attribute

import ac.github.oa.OriginAttribute
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake
import java.util.*

object AttributeManager {

    val attributeInstances = mutableListOf<Attribute>()
    val usableAttributes = sortedMapOf<Int, Attribute>()

    private val table = mutableMapOf<UUID, AttributeData>()

    fun remove(uuid: UUID) {
        table.remove(uuid)
    }

    fun set(uuid: UUID,data: AttributeData) {
        table[uuid] = data
    }

    fun get(player: Player): AttributeData {
        return get(player.uniqueId)
    }

    fun get(uuid: UUID): AttributeData {
        return table[uuid] ?: AttributeData()
    }

    fun loadAttributeClass() {
        runningClasses.forEach {
            if (Attribute::class.java.isAssignableFrom(it)) {
                attributeInstances + it.newInstance()
            }
        }
        attributeInstances.forEach {
            val priority = getPriority(it)
            if (priority != -1) {
                usableAttributes[priority] = it
            }
        }
    }

    @Awake(LifeCycle.ENABLE)
    fun registerAll() {
        attributeInstances.clear()
        usableAttributes.clear()
        this.loadAttributeClass()
        usableAttributes.forEach {
            it.value.onLoad()
        }
    }

    fun getPriority(attribute: Attribute): Int {
        val listOf = OriginAttribute.config.getStringList("attributes").toMutableList()
        listOf.removeIf { s -> attributeInstances.any { it.toName() == s } }
        return listOf.indexOf(attribute.toName())
    }


}
