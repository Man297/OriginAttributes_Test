package ac.github.oa.internal.attribute.impl.other


import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityRegainHealthEvent
import taboolib.common.platform.function.submit

/**
 * 0 回血
 */
class HealthRecovery : SingleAttributeAdapter(AttributeType.OTHER) {
    override fun defaultOption(config: BaseConfig) {
        super.defaultOption(config)
        config.select(this).set("period", 100.0)
    }

    override fun enable() {

        submit(delay = baseConfig.select(this).any("period").asNumber().toLong()) {
            for (player in ArrayList<Player>(Bukkit.getOnlinePlayers())) {
                if (!player.isDead && player.isOnline) {
                    val maxHealth: Double =
                        player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue ?: 0.0
                    if (player.health < maxHealth) {
                        var healthRegen: Double = OriginAttributeAPI.getAttributeData(player)
                            .find(HealthRecovery::class.java)[0].globalEval(maxHealth)
                        if (healthRegen > 0) {
                            val event = EntityRegainHealthEvent(
                                player,
                                healthRegen,
                                EntityRegainHealthEvent.RegainReason.CUSTOM
                            )
                            Bukkit.getPluginManager().callEvent(event)
                            if (!event.isCancelled) {
                                healthRegen =
                                    if (event.amount + player.health > maxHealth) maxHealth - player.health else event.amount
                                player.health = healthRegen + player.health
                            }
                        }
                    }
                }
            }
        }
    }

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {}
    override val strings: Array<String>
        get() = arrayOf("血量恢复")
    override val type: ValueType
        get() = ValueType.ALL
}