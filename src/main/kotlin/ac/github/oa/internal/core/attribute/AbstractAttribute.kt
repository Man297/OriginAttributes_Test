package ac.github.oa.internal.core.attribute

import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.configuration.ConfigFile
import taboolib.module.configuration.Configuration
import java.io.File

abstract class AbstractAttribute : Attribute {

    lateinit var root: ConfigFile
    val entries = mutableListOf<Attribute.Entry>()
    abstract val types: Array<AttributeType>
    open var index = 0

    override fun onLoad() {
        val file = File(getDataFolder(), toRootPath())
        if (!file.exists()) {
            releaseResourceFile(toRootPath(), true)
        }
        root = Configuration.loadFromFile(file)
        this::class.java.declaredFields.forEach {
            if (Attribute.Entry::class.java.isAssignableFrom(it.type)) {
                it.isAccessible = true
                entries += (it.get(this) as Attribute.Entry).apply {
                    this.name = it.name
                    this.node = this@AbstractAttribute
                }
            }
        }
        entries.forEachIndexed { _, entry ->
            entry.onEnable()
        }

        info("${toName()} registered.")

    }

    override fun getPriority(): Int {
        return index
    }

    override fun setPriority(index: Int) {
        this.index = index
    }

    override fun onDisable() {}

    override fun onReload() {}

    fun toRootPath(): String {
        return "attribute/${toLocalName()}.yml"
    }

    override fun toLocalName(): String {
        return toName()
    }

    override fun toName(): String {
        return AbstractAttribute::class.java.simpleName
    }

    override fun getEntry(index: Int): Attribute.Entry {
        return entries[index]
    }

    override fun getEntry(name : String) : Attribute.Entry {
        return toEntities().first { it.name == name }
    }

    override fun toEntrySize(): Int {
        return entries.size
    }

}
