package ac.github.oa.internal.attribute.impl.attack

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.platform.event.PlayerJumpEvent
import java.util.*

class JumpDamage : SingleAttributeAdapter(AttributeType.ATTACK) {

    companion object {

        val cache = Collections.synchronizedList(arrayListOf<Player>())

        @SubscribeEvent
        fun e(e: PlayerJumpEvent) {
            cache.add(e.player)
        }

        @SubscribeEvent
        fun e(e: EntityDamageEvent) {
            val attacker = e.damageMemory.attacker
            if (cache.contains(attacker)){
                val location = attacker.location
                if (attacker.world.getBlockAt(location.add(0.0,-0.5,0.0)).type != Material.AIR){
                    cache.remove(attacker)
                    attacker.isOnGround
                    OriginAttributeAPI.callUpdate(attacker)
                }
            }
        }

    }

    override val strings: Array<String>
        get() = arrayOf("跳跃加成")
    override val type: ValueType
        get() = ValueType.ALL

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        if (eventMemory is DamageMemory && eventMemory.attacker is Player) {
            val attacker = eventMemory.attacker
            if (cache.contains(attacker)) {
                eventMemory.addDamage(baseDoubles[0].number())
                eventMemory.arrow
            }
        }
    }
}