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
                    val locationA = injured.location
                    val locationB = attacker.location
                    val vectorAB = locationB.clone().subtract(locationA).toVector()
                    injured.velocity = vectorAB.multiply(3.0)
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
            info("@Capture ${memory.labels["@Capture"]} $data")
        }

    }

}
