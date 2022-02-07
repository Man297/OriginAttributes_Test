package ac.github.oa.internal.core.item.action.impl

import ac.github.oa.internal.core.item.action.IActionEvent
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import taboolib.module.kether.ScriptContext


object ActionRightBlock : IActionEvent<PlayerInteractEvent>() {

    override val namespace: String
        get() = "on right block"
    override val event: Class<PlayerInteractEvent>
        get() = PlayerInteractEvent::class.java

    override fun test(e: PlayerInteractEvent): Player? {
        return when {
            e.hasItem() && e.hand == EquipmentSlot.HAND && e.action == Action.RIGHT_CLICK_AIR -> {
                e.player
            }
            else -> null
        }
    }

    override fun inject(context: ScriptContext, e: PlayerInteractEvent) {

    }

}