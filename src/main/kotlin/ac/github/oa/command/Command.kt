package ac.github.oa.command

import ac.github.oa.OriginAttribute
import ac.github.oa.api.ItemAPI
import ac.github.oa.api.event.plugin.OriginPluginReloadEvent
import ac.github.oa.internal.core.item.ItemPlant
import ac.github.oa.internal.core.item.random.RandomPlant
import ac.github.oa.internal.core.item.script.content.JavaScriptPlant
import ac.github.oa.internal.core.ui.InfoUI
import org.bukkit.Bukkit
import taboolib.common.platform.*
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper

@CommandHeader("rpg", aliases = ["oa", "rpgo", "originattribute"])
object Command {

    @CommandBody
    val helper = mainCommand {
        createHelper()
    }

    @CommandBody
    val item = CommandItem

    @CommandBody
    val map = CommandMap

    @CommandBody
    val reload = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            OriginAttribute.module.reload()
            ItemPlant.init()
            RandomPlant.init()
            JavaScriptPlant.init()
            OriginAttribute.config.reload()
            Bukkit.getOnlinePlayers().forEach {
                ItemAPI.checkUpdate(it, it.inventory)
            }
            OriginPluginReloadEvent().call()
            sender.sendMessage("reload successful.")
        }
    }


    @CommandBody
    val info = subCommand {
        dynamic(commit = "viewer") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }

            execute<ProxyCommandSender> { _, _, argument ->
                Bukkit.getPlayerExact(argument)?.let {
                    InfoUI(it, it).open()
                }
            }

            dynamic(commit = "target") {
                suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }

                execute<ProxyCommandSender> { sender, context, argument ->
                    InfoUI(Bukkit.getPlayerExact(context.argument(-1))!!, Bukkit.getPlayerExact(argument)!!).open()
                }

            }

        }

    }


}
