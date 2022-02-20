package ac.github.oa.internal.core.item.action.impl

import ac.github.oa.internal.core.item.action.IActionEvent
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import taboolib.module.kether.ScriptContext

object ActionLeftBlock : IActionEvent<PlayerInteractEvent>() {

    override val namespace: String
        get() = "on left block"
    override val event: Class<PlayerInteractEvent>
        get() = PlayerInteractEvent::class.java

    override fun test(e: PlayerInteractEvent): Player? {
        return when {
            e.hasItem() && e.hand == EquipmentSlot.HAND && e.action == Action.LEFT_CLICK_BLOCK -> {
                e.player
            }
            else -> null
        }
    }

}