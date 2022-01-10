package ac.github.oa.internal.attribute.impl.other

import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.internal.attribute.AttributeManager
import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import ac.github.oa.util.isEnabled
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import java.util.concurrent.ConcurrentHashMap

class AttackSpeed : SingleAttributeAdapter(AttributeType.OTHER) {


    companion object {

        var map: MutableMap<Player, Spot> = ConcurrentHashMap<Player, Spot>()

        @SubscribeEvent
        fun e(e: EntityDamageEvent) {

            if (!AttackSpeed::class.java.isEnabled()) return
            val damageMemory: DamageMemory = e.damageMemory
            val attacker: LivingEntity = damageMemory.attacker
            if (attacker is Player) {
                val player: Player = attacker
                if (e.priorityEnum == PriorityEnum.POST) {
                    val base = when (val spot = map[player]) {
                        null -> 1.0
                        else -> {
                            if (spot.isValid()) spot.task() else 1.0
                        }
                    }
                    e.damageMemory.damage = e.damageMemory.damage * base
                    val baseDoubles: Array<BaseDouble> = damageMemory.attackAttributeData.find(AttackSpeed::class.java)
                    val number: Double = baseDoubles[0].number() + 1.0
                    map[player] = Spot((1000 / number).toLong())

                }
            }
        }
    }


    class Spot(val survival: Long) {

        val stamp = System.currentTimeMillis()

        fun isValid(): Boolean {
            return surplus() > 0
        }

        fun surplus(): Long {
            return stamp + survival - System.currentTimeMillis()
        }

        fun task(): Double {
            return 1.0 - surplus().toDouble().div(survival.toDouble())
        }
    }

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {}

    override val strings: Array<String>
        get() = arrayOf("攻击速度")
    override val type: ValueType
        get() = ValueType.NUMBER

}