package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.internal.base.enums.PriorityEnum
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import java.util.*

class AttackSpeed {

    companion object {

        val cache = Collections.synchronizedMap(mutableMapOf<UUID, Cache>())


        @SubscribeEvent(ignoreCancelled = true, priority = EventPriority.HIGH)
        fun e(proxyEvent: EntityDamageEvent) {
            if (proxyEvent.priorityEnum == PriorityEnum.PRE) return

            val attacker = proxyEvent.damageMemory.attacker

            if (cache.containsKey(attacker.uniqueId)) {
                val cache = cache[attacker.uniqueId]!!
                val progress = cache.progress()
                if (progress < 1) {
                    proxyEvent.damageMemory.getDamageSources().forEach {
                        it.value = it.value * progress
                    }
                }
            }

            createCache(attacker)?.let {
                cache[attacker.uniqueId] = it
            }
        }

        fun createCache(entity: LivingEntity): Cache? {
            val attribute = entity.getAttribute(Attribute.GENERIC_ATTACK_SPEED) ?: return null
            val timeMillis = System.currentTimeMillis()
            return Cache(timeMillis, (timeMillis + 1000 / attribute.value).toLong())
        }

    }

    class Cache(val createStamp: Long, val endStamp: Long) {

        fun progress(): Float {
            val millis = System.currentTimeMillis()
            if (millis > endStamp) return 1.0f
            if (millis == createStamp) return 0.0f
            // 等待秒数 = end - create
            // 已过去秒数 = curr - create
            // 进度 = 已过去描述 / 等待秒数
            return (millis - createStamp).toFloat() / (endStamp - createStamp).toFloat()
        }

    }

}
