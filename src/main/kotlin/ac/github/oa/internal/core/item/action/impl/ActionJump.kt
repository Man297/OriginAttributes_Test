package ac.github.oa.internal.core.item.action.impl

import ac.github.oa.api.event.ProxyPlayerJumpEvent
import ac.github.oa.internal.core.item.action.IActionEvent
import org.bukkit.entity.Player
import taboolib.module.kether.ScriptContext
import taboolib.platform.event.PlayerJumpEvent

object ActionJump : IActionEvent<ProxyPlayerJumpEvent>() {

    override val namespace: String
        get() = "on jump"
    override val event: Class<in ProxyPlayerJumpEvent>
        get() = ProxyPlayerJumpEvent::class.java

    override fun test(e: ProxyPlayerJumpEvent): Player? {
        return e.player
    }

    override fun inject(context: ScriptContext, e: ProxyPlayerJumpEvent) {

    }
}