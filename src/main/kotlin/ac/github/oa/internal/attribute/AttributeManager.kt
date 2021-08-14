package ac.github.oa.internal.attribute

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.attribute.AttributeData
import ac.github.oa.internal.base.BaseConfig
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.Listener
import taboolib.platform.BukkitPlugin
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level

object AttributeManager {


    var folder: File = File(BukkitPlugin.getInstance().dataFolder, "attributes")

    var map: MutableMap<UUID, AttributeData> = ConcurrentHashMap()

    var list: MutableList<AttributeAdapter> = ArrayList()
    var attributes: MutableList<AttributeAdapter> = ArrayList()

    operator fun get(uuid: UUID): AttributeData {
        return map.computeIfAbsent(uuid) { u: UUID? -> AttributeData() }
    }

    operator fun set(uuid: UUID, attributeData: AttributeData) {
        map[uuid] = attributeData
    }

    fun getAttributeAdapter(name: String): Optional<AttributeAdapter> {
        return attributes
            .stream()
            .filter { attributeAdapter: AttributeAdapter -> attributeAdapter.name.equals(name, ignoreCase = true) }
            .findFirst()
    }

    fun register(attributeAdapter: AttributeAdapter) {
        list.add(attributeAdapter)
        loadAttribute(attributeAdapter)
    }

    fun loadAttribute(attributeAdapter: AttributeAdapter) {
        /**
         * 获取优先比
         */
        val priority = getPriority(attributeAdapter)
        if (priority == -1) {
            // 属性未启用
            BukkitPlugin.getInstance().logger.log(Level.WARNING, "属性 {0} 未启用.", attributeAdapter.name)
            return
        }
        attributeAdapter.priority = priority
        attributeAdapter.load()
        BukkitPlugin.getInstance().logger.log(Level.INFO, "属性 {0} 启用成功.", attributeAdapter.name)
        attributeAdapter.baseConfig = getBaseConfig(attributeAdapter)
        attributes.add(attributeAdapter)
        attributes.sort()
    }

    fun getBaseConfig(attributeAdapter: AttributeAdapter): BaseConfig {
        val file = getFile(attributeAdapter)
        var yamlConfiguration: YamlConfiguration? = null
        if (!file.exists() && attributeAdapter.createFile) {
            try {
                file.createNewFile()
                yamlConfiguration = YamlConfiguration.loadConfiguration(file)
                attributeAdapter.defaultOption(BaseConfig(null, yamlConfiguration))
                yamlConfiguration.save(file)
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        } else {
            yamlConfiguration = YamlConfiguration.loadConfiguration(file)
        }
        return BaseConfig(null, yamlConfiguration!!)
    }


    fun getPriority(attributeAdapter: AttributeAdapter): Int {
        return BukkitPlugin.getInstance().config.getStringList("attributes").indexOf(attributeAdapter.name)
    }

    fun getFile(attributeAdapter: AttributeAdapter): File {
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return File(folder, attributeAdapter.name + ".yml")
    }

    fun enable(attribute: AttributeAdapter) {
        attribute.enable()
        if (attribute is Listener) {
            Bukkit.getPluginManager().registerEvents((attribute as Listener), BukkitPlugin.getInstance())
        }
    }

    fun reloads() {
        for (attribute in attributes) {
            attribute.baseConfig = getBaseConfig(attribute)
            attribute.reload()
        }
    }
}