package ac.github.oa.internal.core.item.action.impl

import ac.github.oa.internal.core.item.action.IActionEvent
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.module.kether.ScriptContext

object ActionAttacked : IActionEvent<EntityDamageByEntityEvent>() {

    override val namespace: String
        get() = "on attacked"
    override val event: Class<in EntityDamageByEntityEvent>
        get() = EntityDamageByEntityEvent::class.java

    override fun test(e: EntityDamageByEntityEvent): Player? {
        return e.entity as? Player
    }

    override fun inject(context: ScriptContext, e: EntityDamageByEntityEvent) {

    }
}