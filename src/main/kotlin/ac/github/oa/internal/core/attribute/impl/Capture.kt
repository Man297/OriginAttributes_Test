package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.api.event.entity.OriginCustomDamageEvent
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import ac.github.oa.internal.core.attribute.*
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyParticle
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.platform.sendTo
import taboolib.common.util.random
import taboolib.common5.Coerce
import taboolib.platform.util.toProxyLocation
import kotlin.math.max
import kotlin.math.min

class Capture : AbstractAttribute() {


    companion object {

        @SubscribeEvent
        fun onCapture(e: EntityDamageEvent) {
            if (e.priorityEnum == PriorityEnum.POST) {
                val damageMemory = e.damageMemory
                if (damageMemory.labels["@Capture"] == true) {

                    val attacker = damageMemory.attacker
                    val injured = damageMemory.injured
                    // 目标b偏移向目标a
                    val locationA = injured.location.clone()
                    val locationB = attacker.location.clone()
                    locationA.pitch = 0f
                    locationB.pitch = 0f
                    val distance = locationA.distance(locationB)
                    val vectorAB = locationB.clone().subtract(locationA).toVector()
                    vectorAB.length()
                    val step = min(0.1, distance)
                    info("@Capture $step")
                    injured.velocity = vectorAB.multiply(step)
                }
            }
        }

    }

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.ATTACK)

    val capture = object : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            memory.setLabel("@Capture", random(data.get(this)))
        }

    }

}
