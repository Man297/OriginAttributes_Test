package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import ac.github.oa.internal.core.attribute.AbstractAttribute
import ac.github.oa.internal.core.attribute.AttributeData
import ac.github.oa.internal.core.attribute.AttributeType
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.info
import java.util.*

class AttackSpeed : AbstractAttribute() {

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.ATTACK)

    override fun onLoad() {
        this.loadEntry()
    }

    override fun onReload() {

    }

    override fun onDisable() {

    }

    val impl = object : ac.github.oa.internal.core.attribute.Attribute.Entry() {
        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory

            val attacker = memory.attacker
            if (cache.containsKey(attacker.uniqueId)) {
                val cache = cache[attacker.uniqueId]!!
                val progress = cache.progress()
                if (progress < 1) {
                    memory.getDamageSources().forEach {
                        it.value = it.value * progress
                    }
                }
            }

            createCache(attacker)?.let {
                cache[attacker.uniqueId] = it
            }
        }

        override val combatPower: Double
            get() = 0.0

        override fun toValue(entity: LivingEntity, args: String, data: AttributeData.Data): Any? {
            return "n/o"
        }

        override fun getKeywords(): List<String> {
            return listOf()
        }

    }


    companion object {

        val cache = Collections.synchronizedMap(mutableMapOf<UUID, Cache>())

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
