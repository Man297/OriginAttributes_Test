package ac.github.oa.internal.attribute.impl.other

import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import taboolib.common.platform.SubscribeEvent
import java.util.concurrent.ConcurrentHashMap

class AttackSpeed : SingleAttributeAdapter(AttributeType.OTHER), Listener {
    @SubscribeEvent
    fun e(e: EntityDamageEvent) {
        val damageMemory: DamageMemory = e.damageMemory
        val attacker: LivingEntity = damageMemory.attacker
        if (attacker is Player) {
            val player: Player = attacker
            if (e.priorityEnum == PriorityEnum.PRE) {
                if (end(player)) {
                    val baseDoubles: Array<BaseDouble> = damageMemory.attackAttributeData.find(AttackSpeed::class.java)
                    val number: Double = baseDoubles[0].number()
                    if (number <= 0) {
                        return
                    }
                    val value = 1000 / number
                    insert(player, (System.currentTimeMillis() + value).toLong())
                } else {
                    e.isCancelled = (true)
                }
            }
        }
    }

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {}

    override val strings: Array<String>
        get() = arrayOf("攻击速度")
    override val type: ValueType
        get() = ValueType.NUMBER

    companion object {

        var map: MutableMap<Player, Long> = ConcurrentHashMap<Player, Long>()
        fun insert(player: Player, stamp: Long) {
            map[player] = stamp
        }

        fun end(player: Player): Boolean {
            if (!map.containsKey(player)) {
                return true
            }
            val aLong = map[player]
            val currentTimeMillis = System.currentTimeMillis()
            return currentTimeMillis > aLong!!
        }
    }
}