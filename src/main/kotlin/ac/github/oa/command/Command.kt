package ac.github.oa.command

import ac.github.oa.OriginAttribute
import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.attribute.AttributeManager
import ac.github.oa.internal.core.container.SellContainer
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
import taboolib.common.platform.function.info
import taboolib.common.platform.function.onlinePlayers
import taboolib.common5.Coerce
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.SecuredFile
import taboolib.module.nms.getItemTag
import taboolib.platform.event.PlayerJumpEvent
import taboolib.platform.util.sendLang
import java.io.File

object Command {


    val map = mutableMapOf<Player, Boolean>()

    fun isDebugEnable(player: Player) = map[player] ?: false

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
                                val itemKey = context.argument(-1)
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
                    dynamic(commit = "item") {
                        suggestion<ProxyCommandSender> { sender, context ->
                            ItemPlant.configs.map { it.name }
                        }
                        execute<ProxyCommandSender> { sender, context, argument ->
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
                                execute<ProxyCommandSender> { sender, context, argument ->
                                    val playerExact = Bukkit.getPlayerExact(context.argument(-3))!!
                                    val item = context.argument(-2)
                                    val map = OriginAttribute.json.fromJson<Map<String, String>>(
                                        argument.replace("/s", " "),
                                        Map::class.java
                                    )
                                    giveItem(
                                        playerExact,
                                        item,
                                        Coerce.toInteger(context.argument(-1)),
                                        map.toMutableMap()
                                    )
                                }
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
                    info(asString)
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

            literal("debug") {
                execute<Player> { sender, _, _ ->
                    map[sender] = !isDebugEnable(sender)
                    sender.sendMessage("Debug status switch to " + isDebugEnable(sender))
                }
            }

            literal("sell") {
                dynamic {
                    suggestion<ProxyCommandSender> { _, _ ->
                        onlinePlayers().map { it.name }
                    }
                    execute<ProxyCommandSender> { _, _, argument ->
                        Bukkit.getPlayerExact(argument)?.let { SellContainer(it).open() }
                    }
                }
            }

            literal("test") {
                execute<Player> { sender, context, argument ->
                    OriginAttributeAPI.setExtra(
                        sender.uniqueId,
                        "test0",
                        OriginAttributeAPI.loadList(listOf("经验加成 +20%"))
                    )
                    OriginAttributeAPI.callUpdate(sender)
                    sender.sendMessage("call")
                }
            }

        }
    }

    fun giveItem(sender: Player, item: String, amount: Int, map: MutableMap<String, String> = mutableMapOf()) {
        if (!ItemPlant.hasKey(item)) {
            sender.sendLang("item-key-is-null")
            return
        }


        val items = createItems(sender, item, amount, map).onEach { sender.inventory.addItem(it) }
        mutableMapOf<String, Int>().apply {

            items.map {
                val displayName = it.itemMeta!!.displayName
                this[displayName] = (this[displayName] ?: 0) + it.amount
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
    ): List<ItemStack> {
        val list = mutableListOf<ItemStack>()
        (0 until amount).forEach { _ ->
            list.add(ItemPlant.build(target, key, map) ?: XMaterial.AIR.parseItem()!!)
        }
        return list
    }

    class ItemData(val itemStack: ItemStack)

}