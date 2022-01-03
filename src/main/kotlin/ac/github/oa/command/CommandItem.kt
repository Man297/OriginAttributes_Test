package ac.github.oa.command

import ac.github.oa.command.CommandItem.openSubManageMenu
import ac.github.oa.internal.core.item.ItemPlant
import ac.github.oa.internal.core.item.ItemPlant.cacheFiles
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.info
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.ClickType
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import java.io.File
import kotlin.math.ceil

@CommandHeader("rpgItem", aliases = ["item", "ri"])
object CommandItem {

    val slots = IntRange(0, 54).map { it }


    @CommandBody
    val manager = subCommand {

        execute<Player> { sender, context, argument ->
            sender.openManageMenu()
        }
    }

    fun Player.openManageMenu() {

        val cacheFiles = ItemPlant.cacheFiles.map { ItemRootFile(it.key, it.value) }
        openMenu<Linked<ItemRootFile>>(title = "§dItemManager") {
            elements { cacheFiles }
            rows(ceil(cacheFiles.size / 9.0).toInt())
            slots(slots)
            cacheFiles.forEachIndexed { index, itemRootFile ->
                set(index, buildItem(XMaterial.BOOK) {
                    name = "§7${itemRootFile.file.name} §f*${itemRootFile.keys.size}"
                })
            }

            onClick { _, element ->
                info("click")
                openSubManageMenu(element)
            }

        }
    }

    fun Player.openSubManageMenu(itemRootFile: ItemRootFile) {
        val itemElements = itemRootFile.keys.map { ItemElement(itemRootFile, it) }
        openMenu<Linked<ItemElement>>(title = "§d${itemRootFile.file.name}") {
            slots(slots)
            rows(ceil(itemElements.size / 9.0).toInt())
            elements { itemElements }
            itemElements.forEachIndexed { index, itemElement ->
                set(index, itemElement.build(this@openSubManageMenu)!!)
            }

            onClick { _, element ->
                this@openSubManageMenu.inventory.addItem(element.build(this@openSubManageMenu)!!)
            }

        }
    }

    class ItemRootFile(val file: File, val keys: List<String>)

    class ItemElement(val root: ItemRootFile, val key: String) {
        fun build(player: Player) = ItemPlant.build(player, key)
    }


}