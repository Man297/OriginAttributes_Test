package ac.github.oa.internal.attribute.impl.attack

import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.event.SubscribeEvent
import kotlin.math.max
import kotlin.math.roundToLong

/**
 * 0 吸血几率
 * 1 吸血量
 */
class BloodSucking : AttributeAdapter(2, AttributeType.ATTACK) {

    override fun defaultOption(config: BaseConfig) {
        config.select(this)
            .setStrings("吸血几率")
            .set("combat-power",1)
            .superior()
            .select("blood-sucking-value")
            .set("combat-power",1)
            .setStrings("吸血量")
    }

    override fun inject(entity: LivingEntity?, string: String, baseDoubles: Array<BaseDouble>) {
        baseDoubles[0].merge(baseConfig.analysis(this, string, ValueType.PERCENT))
        baseDoubles[1].merge(baseConfig.analysis("blood-sucking-value", string, ValueType.ALL))
    }

    @SubscribeEvent
    fun e(e: EntityDamageEvent) {
        val priorityEnum = e.priorityEnum
        if (priorityEnum == PriorityEnum.POST && !e.isCancelled() && e.damageMemory.labels.containsKey(BloodSucking::class.java)) {
            val any = e.damageMemory.labels[BloodSucking::class.java]!!.toString().toDouble()
            e.damageMemory.attacker.health =
                max(e.damageMemory.attacker.health + any, e.damageMemory.attacker.maxHealth)
        }
    }

    override fun format(entity: LivingEntity, s: String, baseDoubles: Array<BaseDouble>): Any {
        return baseDoubles[if (s.equals("p", ignoreCase = true)) 0 else 1].number()
    }

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        if (eventMemory is DamageMemory) {
            val damageMemory: DamageMemory = eventMemory
            if (baseDoubles[0].random()) {
                val eval = baseDoubles[0].globalEval(damageMemory.damage)
                damageMemory.setLabel(BloodSucking::class.java, eval)

            }
        }
    }

    override fun count(baseDoubles: Array<BaseDouble>): Long {
        return (baseDoubles[0].number() * baseConfig.select(this).any("combat-power").asNumber().toDouble() +
                baseDoubles[1].number() * baseConfig.select("blood-sucking-value").any("combat-power").asNumber()
            .toDouble()
                ).roundToLong()
    }
}