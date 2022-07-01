package ac.github.oa.internal.core.attribute

import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.ConfigFile
import taboolib.module.configuration.Configuration
import java.io.File

@Abstract
abstract class AbstractAttribute : Attribute {

    open lateinit var root: ConfigurationSection

    val entries = mutableListOf<Attribute.Entry>()

    abstract val types: Array<AttributeType>

    open var index = -1

    override fun onLoad() {
        this.loadFile()
        this.loadEntry()
    }

    open fun loadEntry() {
        this::class.java.declaredFields.forEach {
            if (Attribute.Entry::class.java.isAssignableFrom(it.type)) {
                it.isAccessible = true
                entries += (it.get(this) as Attribute.Entry).apply {
                    this.name = it.name
                    this.index = entries.size
                    this.node = this@AbstractAttribute
                }
            }
        }
        entries.forEachIndexed { _, entry ->
            entry.onEnable()
        }
        info("|- Registered attribute ${toName()}.")
    }

    open fun toDataFolder(): File {
        return getDataFolder()
    }

    open fun releaseResourceFile() {
        releaseResourceFile(toRootPath(), false)
    }

    open fun loadFile() {
        val file = File(toDataFolder(), toRootPath())
        releaseResourceFile()
        root = Configuration.loadFromFile(file)
    }

    override fun getPriority(): Int {
        return index
    }

    override fun setPriority(index: Int) {
        this.index = index
    }

    override fun toRoot(): ConfigurationSection {
        return root
    }

    override fun onDisable() {}

    override fun onReload() {
        this.loadFile()
    }

    fun toRootPath(): String {
        return "attribute/${toLocalName()}.yml"
    }

    override fun toLocalName(): String {
        return toName()
    }

    override fun toName(): String {
        return this::class.java.simpleName
    }

    override fun getEntry(index: Int): Attribute.Entry {
        return entries[index]
    }

    override fun getEntry(name: String): Attribute.Entry {
        return toEntities().firstOrNull { it.name == name } ?: error("${this.toName()}: $name not found.")
    }

    override fun toEntrySize(): Int {
        return entries.size
    }

}
