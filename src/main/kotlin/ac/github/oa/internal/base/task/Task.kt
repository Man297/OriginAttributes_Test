package ac.github.oa.internal.base.task

import ac.github.oa.internal.base.enums.ActionType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.entity.Player
import java.util.concurrent.atomic.AtomicInteger
import ac.github.oa.OriginAttribute
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import java.util.ArrayList
import java.util.HashMap
import java.util.function.Consumer

class Task(var actionType: ActionType, var amount: Int, var consumer: Consumer<Int>) {

    var platformTask: PlatformExecutor.PlatformTask? = null

    fun start(player: Player) {
        val tasks = map.computeIfAbsent(player) { p: Player? -> ArrayList() }
        tasks.removeIf { task: Task ->
            val b = task.actionType == actionType
            if (b) {
                task.platformTask?.cancel()
            }
            b
        }
        tasks.add(this)
        val integer = AtomicInteger(0)

        val platformTask = submit(delay = 20) {
            val i = integer.get()
            if (i >= amount) {
                cancel()
            } else {
                consumer.accept(i)
                integer.getAndIncrement()
            }
        }

    }

    companion object {
        var map: MutableMap<Player, MutableList<Task>> = HashMap()
    }
}