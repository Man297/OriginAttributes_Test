package ac.github.oa.api.event

import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.event.PlayerJumpEvent
import taboolib.platform.type.BukkitProxyEvent

class ProxyPlayerJumpEvent(val player: Player) : BukkitProxyEvent() {

    companion object {

        @SubscribeEvent(ignoreCancelled = true)
        fun e(e : PlayerJumpEvent) {
            ProxyPlayerJumpEvent(e.player).call()
        }

    }

}