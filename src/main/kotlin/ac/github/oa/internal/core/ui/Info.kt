package ac.github.oa.internal.core.ui

import ac.github.oa.api.event.plugin.OriginPluginReloadEvent
import ac.github.oa.internal.core.attribute.AttributeData
import ac.github.oa.internal.core.attribute.AttributeManager
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XMaterial
import taboolib.library.xseries.getItemStack
import taboolib.module.chat.colored
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.platform.util.buildItem
import taboolib.platform.util.isAir

object Info {

    val AIR_ICON = buildItem(XMaterial.BARRIER) {
        name = " "
        lore.clear()
    }

    @Config("info.yml", migrate = true)
    lateinit var config: Configuration

    val icons = mutableListOf<IconItem>()

    val rows: Int
        get() = config.getInt("__option__.rows", 6)

    val title: String
        get() = config.getString("__option__.name")!!

    @Awake(LifeCycle.ENABLE)
    fun load() {
        icons.clear()
        config.getKeys(false).forEach {
            if (it != "__option__") {
                icons += IconItem(config.getConfigurationSection(it)!!)
            }
        }
    }

    @SubscribeEvent
    fun e(e: OriginPluginReloadEvent) {
        config.reload()
        this.load()
    }

    fun toTitle(viewer: Player, target: Player): String {
        return title.replace("{viewer}", viewer.name)
            .replace("{target}", target.name)
    }

    class IconItem(val config: ConfigurationSection) {


        val equipment: String
            get() = config.getString("equipment", "custom")!!

        val slots: List<Int>
            get() = config.getIntegerList("slots")

        val airIcon: ItemStack
            get() = config.getItemStack("air-icon") ?: AIR_ICON

        fun toIcon(player: Player): ItemStack {
            val data = AttributeManager.get(player)
            if (this.equipment == "custom") {
                return buildItem(config.getItemStack("icon")!!) {
                    name = PlaceholderAPI.setPlaceholders(player, name).colored()
                    lore.clear()
                    lore += PlaceholderAPI.setPlaceholders(player, config.getStringList("icon.lore")).colored()
                }
            } else {
                val itemStack = data.items.firstOrNull { it.slot.id == equipment }?.item

                if (itemStack == null || itemStack.isAir()) {
                    return airIcon
                }

                return itemStack
            }

        }

    }


}
