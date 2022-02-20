package ac.github.oa.internal.core.item.action.impl

import ac.github.oa.internal.core.item.action.IActionEvent
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.module.kether.ScriptContext

object ActionAttack : IActionEvent<EntityDamageByEntityEvent>() {

    override val namespace: String
        get() = "on attack"
    override val event: Class<in EntityDamageByEntityEvent>
        get() = EntityDamageByEntityEvent::class.java

    override fun test(e: EntityDamageByEntityEvent): Player? {
        return e.damager as? Player
    }

    override fun inject(context: ScriptContext, e: EntityDamageByEntityEvent) {

    }
}