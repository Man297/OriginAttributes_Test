package ac.github.oa.command

import ac.github.oa.OriginAttribute
import ac.github.oa.api.ItemAPI
import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.core.item.ItemBuilder
import ac.github.oa.internal.core.item.ItemPlant
import ac.github.oa.internal.core.item.random.RandomPlant
import ac.github.oa.internal.core.script.content.JavaScriptPlant
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.*
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.getItemTag
import taboolib.platform.util.sendLang

@CommandHeader("rpg", aliases = ["oa","rpgo","originattribute"])
object Command {


    val map = mutableMapOf<Player, Boolean>()

    fun isDebugEnable(player: Player) = map[player] ?: false

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

    @CommandBody
    val reload = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            ItemPlant.init()
            RandomPlant.init()
            JavaScriptPlant.init()
            OriginAttribute.config.reload()
            Bukkit.getOnlinePlayers().forEach {
                ItemAPI.checkUpdate(it, it.inventory)
            }
            sender.sendMessage("reload successful.")
        }
    }

    @CommandBody
    val debug = subCommand {
        execute<Player> { sender, _, _ ->
            map[sender] = !isDebugEnable(sender)
            sender.sendMessage("Debug status switch to " + isDebugEnable(sender))
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


}
