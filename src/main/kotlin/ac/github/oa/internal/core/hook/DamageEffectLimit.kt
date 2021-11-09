package ac.github.oa.internal.core.hook

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.core.item.random.RandomPlant
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.util.random
import taboolib.platform.BukkitPlugin

object DamageEffectLimit {

    val enable: Boolean
        get() = OriginAttribute.config.getBoolean("options.damage-effect-limit")

    @Awake(LifeCycle.ENABLE)
    fun init() {
        if (enable) {
            val pm = ProtocolLibrary.getProtocolManager()
            pm.addPacketListener(object :
                PacketAdapter(
                    BukkitPlugin.getInstance(),
                    ListenerPriority.NORMAL,
                    PacketType.Play.Server.WORLD_PARTICLES
                ) {
                override fun onPacketSending(event: PacketEvent) {
                    val pc = event.packet
                    val particles = pc.particles.values
                    if (particles.contains(EnumWrappers.Particle.DAMAGE_INDICATOR)) {
                        val max = 10
                        val min = 1
                        val index = particles.indexOf(EnumWrappers.Particle.DAMAGE_INDICATOR)
                        val rand: Int = random(min, max)
                        event.packet.integers.write(index, rand)
                    }
                }
            })
        }

    }

}