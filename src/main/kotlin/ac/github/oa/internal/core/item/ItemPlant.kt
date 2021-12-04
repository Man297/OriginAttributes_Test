package ac.github.oa.internal.core.item

import ac.github.oa.api.event.item.ItemCreatedEvent
import ac.github.oa.util.listFile
import ac.github.oa.util.newfolder
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.SecuredFile
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.getItemTag
import taboolib.platform.BukkitPlugin
import java.util.logging.Level

object ItemPlant {

    const val KEY = "oa-key"

    val generators = arrayListOf<ItemGenerator>()
    val configs = arrayListOf<ConfigurationSection>()

    val folder = newfolder(BukkitPlugin.getInstance().dataFolder, "item", listOf("default.yml"))

    @Awake(LifeCycle.ENABLE)
    fun init() {
        configs.clear()
        folder.listFile("yml").forEach {
            SecuredFile.loadConfiguration(it).apply {
                getKeys(false).forEach {
                    configs.add(this.getConfigurationSection(it)!!)
                }
            }
        }
        BukkitPlugin.getInstance().logger.log(Level.INFO, "|- 共加载物品 ${configs.size} 个配置")
    }

    fun hasKey(key: String): Boolean = configs.any { it.name == key }

    fun build(entity: LivingEntity?, key: String, map: MutableMap<String, String> = mutableMapOf()): ItemStack? =
        build(entity, configs.firstOrNull { it.name == key } ?: throw NullPointerException("未发现${key}对应物品配置."), map)

    fun getConfig(key: String) = configs.first { it.name == key }

    fun build(
        entity: LivingEntity?,
        config: ConfigurationSection,
        map: MutableMap<String, String> = mutableMapOf()
    ): ItemStack {
        val generatorKey = config.getString("g", "")
        val generator = generators.firstOrNull { generatorKey == it.name }
            ?: throw NullPointerException("无效的物品生成器 $generatorKey")

        return generator.build(entity, config, map).apply {
            // 写入nbt
            val itemTag = getItemTag()
            itemTag[KEY] = ItemTagData(config.name)

            val event = ItemCreatedEvent(entity, config, this, generator, itemTag)
            event.call()

            event.itemTag.saveTo(event.itemStack)
        }
    }

    fun parseItem(itemStack: ItemStack): String? {
        val itemTag = itemStack.getItemTag()
        if (itemTag.containsKey(KEY)) {
            return itemTag[KEY]?.asString()!!
        }
        return null
    }


}