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



}
