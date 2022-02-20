package ac.github.oa.internal.core.item.action

import org.bukkit.entity.Player
import org.bukkit.event.Event
import taboolib.module.kether.ScriptContext
import java.util.function.Function

@Suppress("UNCHECKED_CAST")
abstract class IActionEvent<E : Any> {

    abstract val namespace: String

    abstract val event: Class<in E>

    abstract fun test(e: E): Player?

    open fun inject(context: ScriptContext, e: E) {  }

}