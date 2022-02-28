package ac.github.oa.internal.core.item.action

import ac.github.oa.internal.core.item.ItemInstance
import ac.github.oa.internal.core.item.ItemPlant
import org.bukkit.entity.Player
import org.bukkit.event.Event
import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.info
import taboolib.common.platform.function.registerBukkitListener
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.printKetherErrorMessage

object ActionEventLoader {

    val triggers = arrayListOf<IActionEvent<*>>()

    @Awake(LifeCycle.ENABLE)
    fun <E : Any> loadTriggers() {
        runningClasses.forEach {
            if (IActionEvent::class.java.isAssignableFrom(it) && !it.isAnnotationPresent(Skip::class.java)) {
                val iTrigger = (it.getInstance()?.get())
                if (iTrigger != null) {
                    (iTrigger as IActionEvent<*>).register()
                }
            }
        }
    }

    fun ItemInstance.handleEvent(player: Player, actionEvent: IActionEvent<*>, event: Event) {
        val actions = item.actions.filter { it.events.contains(actionEvent.namespace) }
        try {
            actions.forEach {
                KetherShell.eval(it.list, sender = adaptPlayer(player), namespace = listOf("origin")) {
                    set("@Event", event)
                    set("@ItemInstance", this)
                    actionEvent.proxyInject(this, event)
                }
            }
        } catch (e: Throwable) {
            e.printKetherErrorMessage()
        }

    }

    @Suppress("UNCHECKED_CAST")
    fun <E : Any> IActionEvent<E>.proxyInject(context: ScriptContext, event: Event) {
        this.inject(context, event as E)
    }

    @Suppress("UNCHECKED_CAST")
    fun <E : Any> IActionEvent<E>.register() {
        triggers.add(this)
        registerBukkitListener(event, EventPriority.HIGHEST, false) { e ->

            val player = test(e as E) ?: return@registerBukkitListener
            ItemPlant.handleEvent(player, this@register, e)
        }
    }

}
