package ac.github.oa.command

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.core.item.ItemPlant
import ac.github.oa.internal.core.item.random.RandomPlant
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.*
import taboolib.module.nms.getItemTag
import taboolib.platform.util.sendLang

object Command {

    @Awake(LifeCycle.ENABLE)
    fun reg() {
        // rpg
        command("rpg") {
            // get
            literal("get") {

                // [item]
                dynamic {
                    suggestion<Player> { _, _ -> ItemPlant.configs.map { it.name } }

                    // amount = 1
                    execute<Player> { sender, context, argument ->
                        giveItem(sender, argument, 1)
                    }

                    // [amount]
                    dynamic(optional = true) {
                        execute<Player> { sender, context, argument ->
                            giveItem(sender, context.argument(-1)!!, argument.toInt())
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
                    OriginAttribute.config.reload()
                    sender.sendMessage("reload successful.")
                }
            }
        }
    }

    fun giveItem(sender: Player, item: String, amount: Int) {
        if (!ItemPlant.hasKey(item)) {
            sender.sendLang("item-key-is-null")
            return
        }

        val items = createItems(sender, item, amount)
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

    fun createItems(target: LivingEntity, key: String, amount: Int): Map<ItemStack, Int> {
        val mapOf = mutableMapOf<ItemStack, Int>()
        (0 until amount).forEach { _ ->
            val itemStack = ItemPlant.build(target, key)
            if (itemStack != null) {
                mapOf[itemStack] = (mapOf[itemStack] ?: 0) + 1
            }
        }
        return mapOf
    }


//    @SubCommand(value = "get", args = "<item> <amount|1>", description = "@message.command-get")
//    var get: SubCommandAdapter = object : SubCommandAdapter() {
//        fun execute(commandSender: CommandSender?, strings: Array<String>): Boolean {
//            val itemKey = strings[1]
//            if (ItemManager.itemConfigs.containsKey(itemKey)) {
//                if (commandSender is Player) {
//                    val amount = if (strings.size == 3) strings[2].toInt() else 1
//                    val item = giveItem(commandSender, itemKey, amount)
//                    //                    message.sendSenderToYamlConfig(commandSender,
////                            "message.command-execute");
//                    message.sendSenderToYamlConfig(
//                        commandSender, "message.player-item-receive",
//                        Param.newInstance().add("item", item).add("amount", amount)
//                    )
//                } else {
//                    message.sendSenderToYamlConfig(
//                        commandSender,
//                        "message.command-sender-be-player"
//                    )
//                }
//            } else {
//                message.sendSenderToYamlConfig(
//                    commandSender,
//                    "message.item-not-exists",
//                    Param.newInstance().add("item", itemKey)
//                )
//            }
//            return false
//        }
//    }

    //give kunss item 10
//    @SubCommand(value = "give", args = "<player> <item> <amount|1>", description = "@message.command-give")
//    var give: SubCommandAdapter = object : SubCommandAdapter() {
//        fun execute(commandSender: CommandSender?, strings: Array<String>): Boolean {
//            val playerExact = Bukkit.getPlayerExact(strings[1])
//            if (playerExact != null) {
//                val itemKey = strings[2]
//                if (ItemManager.itemConfigs.containsKey(itemKey)) {
//                    val amount = if (strings.size == 4) strings[3].toInt() else 1
//                    val item = giveItem(playerExact, itemKey, amount)
//                    message.sendSenderToYamlConfig(
//                        commandSender,
//                        "message.command-execute"
//                    )
//                    message.sendSenderToYamlConfig(
//                        playerExact, "message.player-item-receive",
//                        Param.newInstance().add("item", item).add("amount", amount)
//                    )
//                } else {
//                    message.sendSenderToYamlConfig(
//                        commandSender,
//                        "message.item-not-exists",
//                        Param.newInstance().add("item", itemKey)
//                    )
//                }
//            } else {
//                message.sendSenderToYamlConfig(commandSender, "message.player-not-online")
//            }
//            return false
//        }
//    }
//
//    fun giveItem(player: Player, string: String, amount: Int): String {
//        val result = AtomicReference(string)
//        IntStream.range(0, amount).forEach { value: Int ->
//            val item: ItemStack = ItemManager.instance.getItem(player, string)
//            result.set(item.itemMeta!!.displayName)
//            player.inventory.addItem(item)
//        }
//        return result.get()
//    }

//    @SubCommand(value = "reload", description = "@message.command-reload", priority = 999.0)
//    var reload: SubCommandAdapter = object : SubCommandAdapter() {
//        fun execute(commandSender: CommandSender?, strings: Array<String?>?): Boolean {
//            AriaAttribute.instance.reloadConfig()
//            ItemManager.instance.load()
//            RandomStringManager.instance.loadData()
//            AttributeManager.reloads()
//            AriaPluginReloadEvent().call()
//            message.sendSenderToYamlConfig(
//                commandSender,
//                "message.command-reload-success"
//            )
//            return false
//        }
//    }
}