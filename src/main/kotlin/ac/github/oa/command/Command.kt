package ac.github.oa.command

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.attribute.AttributeManager
import ac.github.oa.internal.core.item.ItemPlant
import ac.github.oa.internal.core.item.random.RandomPlant
import ac.github.oa.internal.core.script.content.JavaScriptPlant
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.io.newFile
import taboolib.common.platform.*
import taboolib.common.platform.command.command
import taboolib.common5.Coerce
import taboolib.module.configuration.SecuredFile
import taboolib.module.nms.getItemTag
import taboolib.platform.event.PlayerJumpEvent
import taboolib.platform.util.sendLang
import java.io.File

object Command {

    @Awake(LifeCycle.ENABLE)
    fun reg() {
        // rpg
        command("rpg") {
            // get
            literal("get") {

                // [item]
                // demo = def0 {"品质": "战士"}
                dynamic {
                    suggestion<Player> { _, _ -> ItemPlant.configs.map { it.name } }

                    // amount = 1
                    execute<Player> { sender, _, argument ->
                        giveItem(sender, argument, 1)
                    }

                    dynamic {
                        execute<Player> { sender, context, argument ->
                            try {
                                val itemKey = context.argument(-1)!!
                                val map = OriginAttribute.json.fromJson<Map<String, String>>(
                                    argument.replace("/s", " "),
                                    Map::class.java
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
                                giveItem(sender, context.argument(-1)!!, argument.toInt())
                            } catch (e: Exception) {
                            }
                        }

                        dynamic {
                            execute<Player> { sender, context, argument ->
                                try {
                                    val itemKey = context.argument(-2)!!
                                    val amount = context.argument(-1)!!
                                    val map = OriginAttribute.json.fromJson<Map<String, String>>(
                                        argument.replace("/s", " "),
                                        Map::class.java
                                    )
                                    giveItem(sender, itemKey, amount.toInt(), map.toMutableMap())
                                } catch (e: Exception) {
                                }
                            }
                        }

                    }
                }
            }

            // give
            literal("give") {
                dynamic {
                    suggestion<ProxyCommandSender> { sender, context ->
                        Bukkit.getOnlinePlayers().map { it.name }
                    }

                    // [item]
                    dynamic {
                        suggestion<ProxyCommandSender> { sender, context ->
                            ItemPlant.configs.map { it.name }
                        }
                        execute<ProxyCommandSender> { sender, context, argument ->
                            val playerExact = Bukkit.getPlayerExact(context.argument(-1)!!)!!
                            giveItem(playerExact, argument, 1)
                        }
                        dynamic {
                            execute<ProxyCommandSender> { sender, context, argument ->
                                val playerExact = Bukkit.getPlayerExact(context.argument(-2)!!)!!
                                val item = context.argument(-1)!!
                                giveItem(playerExact, item, Coerce.toInteger(argument))
                            }
                        }
                    }
                }
            }

            literal("save") {

                // file
                dynamic {

                    // key
                    dynamic {
                        execute<Player> { sender, context, argument ->
                            val filename = context.argument(0)!!
                            val newFile = newFile(ItemPlant.folder, "$filename .yml")
                            if (!newFile.exists()) {
                                newFile.createNewFile()
                            }
                            val securedFile = SecuredFile.loadConfiguration(newFile)
                        }
                    }

                }

            }

            // nbt
            literal("nbt") {
                execute<Player> { sender, context, argument ->
                    val mainHand = sender.inventory.itemInMainHand
                    if (mainHand.type == Material.AIR) {
                        sender.sendMessage("hand is null.")
                        return@execute
                    }
                    val itemTag = mainHand.getItemTag()
                    val asString = itemTag.asString()
                    sender.sendMessage("json = $asString")
                }
            }

            literal("reload") {
                execute<ProxyCommandSender> { sender, context, argument ->
                    ItemPlant.init()
                    RandomPlant.init()
                    JavaScriptPlant.init()
                    OriginAttribute.config.reload()
                    sender.sendMessage("reload successful.")
                }
            }

            literal("attrs") {
                execute<ProxyCommandSender> { sender, _, _ ->
                    sender.sendMessage("Enable attributes [" + AttributeManager.attributes.joinToString(",") { it.name } + "]")
                }
            }

        }
    }

    fun giveItem(sender: Player, item: String, amount: Int, map: MutableMap<String, String> = mutableMapOf()) {
        if (!ItemPlant.hasKey(item)) {
            sender.sendLang("item-key-is-null")
            return
        }

        val items = createItems(sender, item, amount, map)
        items.forEach { sender.inventory.addItem(it.key) }
        mutableMapOf<String, Int>().apply {
            items.map {
                val displayName = it.key.itemMeta!!.displayName
                this[displayName] = (this[displayName] ?: 0) + it.value
            }

            this.forEach {
                sender.sendLang("sender-get-item", it.key, it.value)
            }
        }
    }

    fun createItems(
        target: LivingEntity,
        key: String,
        amount: Int,
        map: MutableMap<String, String> = mutableMapOf()
    ): Map<ItemStack, Int> {
        val mapOf = mutableMapOf<ItemStack, Int>()
        (0 until amount).forEach { _ ->
            val itemStack = ItemPlant.build(target, key, map)
            if (itemStack != null) {
                mapOf[itemStack] = (mapOf[itemStack] ?: 0) + 1
            }
        }
        return mapOf
    }
}