package ac.github.oa.internal.core.item.action.impl

import ac.github.oa.internal.core.item.action.IActionEvent
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEntityEvent

object ActionInteractEntity : IActionEvent<PlayerInteractEntityEvent>() {
    override val namespace: String
        get() = "on interact entity"
    override val event: Class<in PlayerInteractEntityEvent>
        get() = PlayerInteractEntityEvent::class.java

    override fun test(e: PlayerInteractEntityEvent): Player? {
        return e.player
    }
}