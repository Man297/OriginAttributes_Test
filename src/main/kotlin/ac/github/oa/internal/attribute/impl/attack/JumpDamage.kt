package ac.github.oa.internal.attribute.impl.attack

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

    }

    var task: PlatformExecutor.PlatformTask? = null

    fun Player.isJump() = cache.contains(this)

    override fun enable() {
        super.enable()
        task = submit(period = 5, async = true) {
            cache.removeIf {
                val location = it.location
                it.world.getBlockAt(location.clone().add(0.0, 0.5, 0.0)).type != Material.AIR
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
            if (attacker.isJump()) {
                eventMemory.addDamage(baseDoubles[0].eval(eventMemory.damage))
            }
        }
    }
}