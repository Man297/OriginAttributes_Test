package ac.github.oa.command

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command

class Command {

    @Awake(LifeCycle.ENABLE)
    fun reg() {
        command("rpg") {

            literal("get") {
                execute<ProxyPlayer> { sender, context, argument ->

                }
            }
            dynamic {
                suggestion<ProxyCommandSender> { sender, context ->
                    listOf("reload")
                }
            }

        }
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