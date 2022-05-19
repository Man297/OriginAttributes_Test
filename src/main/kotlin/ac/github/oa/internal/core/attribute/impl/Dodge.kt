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

class Dodge : AbstractAttribute() {


    companion object {

        val dodgeInstance: Dodge
            get() = AttributeManager.getAttribute("Dodge") as Dodge

        @SubscribeEvent(ignoreCancelled = true)
        fun onDodge(e: EntityDamageEvent) {
            if (e.priorityEnum == PriorityEnum.POST) {
                val damageMemory = e.damageMemory
                if (damageMemory.labels["@Dodge"] == true) {

                    if (damageMemory.labels["@Capture"] == false) {
                        dodgeInstance.dodge.sendTo(e.damageMemory.injured)
                    }

                    e.damageMemory.event.isCancelled = true
                    e.isCancelled = true
                }
            }
        }

    }

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.DEFENSE)


    val dodge = DefaultImpl()

    class DefaultImpl : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE

        val isAction: Boolean
            get() = node.toRoot().getBoolean("${name}.action", false)

        val particle: ProxyParticle?
            get() = node.toRoot().getEnum("${name}.particle", ProxyParticle::class.java)!!

        val step: Double
            get() = node.toRoot().getDouble("${name}.step")

        val volume: Float
            get() = Coerce.toFloat(node.toRoot()["${name}.volume"])

        val pitch: Float
            get() = Coerce.toFloat(node.toRoot()["${name}.pitch"])

        val sound: Sound
            get() = node.toRoot().getEnum("${name}.sound", Sound::class.java)!!

        fun sendTo(entity: LivingEntity) {
            val clone = entity.location.clone()
            clone.pitch = 0f

            val direction = clone.direction
            direction.multiply(step)
            entity.velocity = direction
            if (entity is Player) {
                entity.playSound(entity.location, sound, volume, pitch)
            }
            particle?.sendTo(entity.location.toProxyLocation(), count = 20, speed = 0.5)

        }

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            if (isAction) {
                memory.setLabel("@Dodge", random(data.get(0) / 100))
            }

        }


    }

}
