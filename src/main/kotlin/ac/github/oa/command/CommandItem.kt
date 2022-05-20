package ac.github.oa.command

import ac.github.oa.OriginAttribute
import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.command.CommandItem.openSubManageMenu
import ac.github.oa.internal.core.item.ItemPlant
import ac.github.oa.internal.core.item.ItemPlant.cacheFiles
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import taboolib.expansion.createHelper
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.getItemTag
import taboolib.module.ui.ClickType
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import taboolib.platform.util.sendLang
import java.io.File
import kotlin.math.ceil

@CommandHeader("item", aliases = ["item", "ri"])
object CommandItem {

    @CommandBody
    val helper = mainCommand {
        createHelper()
    }

    val slots = IntRange(0, 54).map { it }

    @CommandBody
    val get = subCommand {
        dynamic {
            suggestion<Player> { _, _ -> ItemPlant.configs.map { it.key } }

            // amount = 1
            execute<Player> { sender, _, argument ->
                giveItem(sender, argument, 1)
            }

            dynamic {
                execute<Player> { sender, context, argument ->
                    try {
                        val itemKey = context.argument(-1)
                        val map = OriginAttribute.json.fromJson<Map<String, String>>(
                            argument.replace("/s", " "), Map::class.java
                        )
                        giveItem(sender, itemKey, 1, map.toMutableMap())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            // [amount]
            dynamic(optional = true) {
                execute<Player> { sender, context, argument ->
                    try {
                        giveItem(sender, context.argument(-1), argument.toInt())
                    } catch (_: Exception) {
                    }
                }

                dynamic {
                    execute<Player> { sender, context, argument ->
                        try {
                            val itemKey = context.argument(-2)
                            val amount = context.argument(-1)
                            val map = OriginAttribute.json.fromJson<Map<String, String>>(
                                argument.replace("/s", " "), Map::class.java
                            )
                            giveItem(sender, itemKey, amount.toInt(), map.toMutableMap())
                        } catch (_: Exception) {
                        }
                    }
                }

            }
        }
    }

    @CommandBody
    val give = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { _, _ ->
                Bukkit.getOnlinePlayers().map { it.name }
            }

            // [item]
            dynamic(commit = "item") {
                suggestion<ProxyCommandSender> { _, _ ->
                    ItemPlant.configs.map { it.key }
                }
                execute<ProxyCommandSender> { _, context, argument ->
                    val playerExact = Bukkit.getPlayerExact(context.argument(-1))!!
                    giveItem(playerExact, argument, 1)
                }
                dynamic() {
                    execute<ProxyCommandSender> { _, context, argument ->
                        val playerExact = Bukkit.getPlayerExact(context.argument(-2))!!
                        val item = context.argument(-1)
                        giveItem(playerExact, item, Coerce.toInteger(argument))
                    }

                    // options
                    dynamic {
                        execute<ProxyCommandSender> { _, context, argument ->
                            val playerExact = Bukkit.getPlayerExact(context.argument(-3))!!
                            val item = context.argument(-2)
                            val map = OriginAttribute.json.fromJson<Map<String, String>>(
                                argument.replace("/s", " "), Map::class.java
                            )
                            giveItem(
                                playerExact, item, Coerce.toInteger(context.argument(-1)), map.toMutableMap()
                            )
                        }
                    }

                }
            }
        }
    }

    @CommandBody
    val nbt = subCommand {
        execute<Player> { sender, context, argument ->
            val mainHand = sender.inventory.itemInMainHand
            if (mainHand.type == Material.AIR) {
                sender.sendMessage("hand is null.")
                return@execute
            }
            val itemTag = mainHand.getItemTag()
            val asString = itemTag.asString()
            sender.sendMessage("json = $asString")
            info(asString)
        }
    }

    fun giveItem(sender: Player, item: String, amount: Int, map: MutableMap<String, String> = mutableMapOf()) {
        if (!ItemPlant.hasKey(item)) {
            sender.sendLang("item-key-is-null")
            return
        }
        val items = ItemPlant.getConfig(item)!!.builder().to(sender, amount, map)
        mutableMapOf<String, Int>().apply {
            items.map {
                val displayName = it.itemMeta!!.displayName
                this[displayName] = (this[displayName] ?: 0) + it.amount
            }

            this.forEach {
                sender.sendLang("sender-get-item", it.key, it.value)
            }
        }
        OriginAttributeAPI.callUpdate(sender)
    }

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
