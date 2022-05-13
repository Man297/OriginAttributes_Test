package ac.github.oa.internal.core.attribute

import ac.github.oa.OriginAttribute
import ac.github.oa.api.event.plugin.AttributeLoadEvent
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import java.lang.reflect.Modifier
import java.util.*

object AttributeManager {

    val attributeInstances = mutableListOf<Attribute>()
    val usableAttributes = sortedMapOf<Int, Attribute>()


    @Config("attribute/config.yml")
    lateinit var config: Configuration


    private val table = mutableMapOf<UUID, AttributeData>()

    fun remove(uuid: UUID) {
        table.remove(uuid)
    }

    fun set(uuid: UUID, data: AttributeData) {
        table[uuid] = data
    }

    fun get(player: Player): AttributeData {
        return get(player.uniqueId)
    }

    fun get(uuid: UUID): AttributeData {
        return table[uuid] ?: AttributeData()
    }

    fun getAttribute(clazz: Class<*>): Attribute {
        return attributeInstances.first { it::class.java == clazz }
    }

    fun getAttribute(name: String): Attribute {
        return attributeInstances.first { it.toName() == name }
    }

    fun loadAttributeClass() {
        runningClasses.forEach {

            if (!Modifier.isInterface(it.modifiers) && !Modifier.isAbstract(it.modifiers) && !it.isAnnotationPresent(
                    Abstract::class.java
                ) && Attribute::class.java.isAssignableFrom(it)
            ) {
                attributeInstances += it.newInstance() as Attribute
            }
        }
        attributeInstances.forEach {
            val priority = getPriority(it)
            if (priority != -1) {
                it.setPriority(priority)
                usableAttributes[priority] = it
            }
        }
    }

    @Awake(LifeCycle.ENABLE)
    fun registerAll() {
        this.loadAttributeClass()
        usableAttributes.forEach {
            enableAttribute(it.value)
        }
    }

    fun enableAttribute(attribute: Attribute) {
        attribute.onLoad()
    }

    fun getPriority(attribute: Attribute): Int {
        val listOf = OriginAttribute.config.getStringList("attributes").toMutableList()
//        listOf.removeIf { s -> s !in attributeInstances.map { it.toName() } }
        return listOf.indexOf(attribute.toName())
    }


}
