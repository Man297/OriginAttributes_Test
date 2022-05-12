package ac.github.oa.internal.core.item

import ac.github.oa.api.event.item.ItemCreatedEvent
import ac.github.oa.internal.core.attribute.AttributeManager
import ac.github.oa.internal.core.item.action.ActionEventLoader.handleEvent
import ac.github.oa.internal.core.item.action.IActionEvent
import ac.github.oa.internal.core.item.generator.ItemGenerator
import ac.github.oa.util.listFile
import ac.github.oa.util.newfolder
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.SecuredFile
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.getItemTag
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.isAir
import java.io.File
import java.util.logging.Level

object ItemPlant {

    const val KEY = "oa-key"
    const val HASCODE = "oa-hasCode"

    val generators = arrayListOf<ItemGenerator>()
    val configs = arrayListOf<Item>()

    val folder = newfolder(BukkitPlugin.getInstance().dataFolder, "item", listOf("default.yml"))
    val cacheFiles = mutableMapOf<File, List<String>>()

    @Awake(LifeCycle.ENABLE)
    fun init() {
        configs.clear()
        cacheFiles.clear()
        folder.listFile("yml").forEach { file ->
            SecuredFile.loadConfiguration(file).apply {
                getKeys(false).apply { cacheFiles[file] = this.toMutableList() }.forEach {
                    configs += Item(this.getConfigurationSection(it)!!)
                }
            }
        }
        BukkitPlugin.getInstance().logger.log(Level.INFO, "|- 共加载物品 ${configs.size} 个配置")
    }

    fun hasKey(key: String): Boolean = configs.any { it.key == key }

    fun build(entity: LivingEntity?, key: String, map: MutableMap<String, String> = mutableMapOf()): ItemStack? =
        build(entity, configs.firstOrNull { it.key == key } ?: throw NullPointerException("未发现${key}对应物品配置."), map)

    fun getConfig(key: String): Item? = configs.firstOrNull { it.key == key }

    fun build(
        entity: LivingEntity?,
        item: Item,
        map: MutableMap<String, String> = mutableMapOf()
    ): ItemStack {
        val generator = generators.firstOrNull { item.generator == it.name }
            ?: throw NullPointerException("无效的物品生成器 ${item.generator}")
        return generator.build(entity, item, map).apply {
            // 写入nbt
            val itemTag = getItemTag()
            itemTag[KEY] = ItemTagData(item.config.name)
            itemTag[HASCODE] = ItemTagData(item.hasCode)

            val event = ItemCreatedEvent(entity, item, this, generator, itemTag)
            event.call()

            event.itemTag.saveTo(event.itemStack)
        }
    }

    fun parseItem(itemStack: ItemStack?): Item? {
        if (itemStack == null || itemStack.isAir()) return null
        val itemTag = itemStack.getItemTag()
        if (itemTag.containsKey(KEY)) {
            return getConfig(itemTag[KEY]?.asString() ?: "__null__")
        }
        return null
    }

    fun handleEvent(player: Player, action: IActionEvent<*>, event: Any) {

        AttributeManager.get(player).items
            .mapNotNull { it.instance() }
            .forEach { it.handleEvent(player, action, event as Event) }
    }

}
