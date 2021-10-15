package ac.github.oa.internal.attribute.impl.defense

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.attribute.impl.other.ShieldRecovery
import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import kotlin.math.roundToLong

/**
 *  0 最大护盾
 *  1 阻挡伤害
 */
class Shield : AttributeAdapter(3, AttributeType.DEFENSE) {
    override fun defaultOption(config: BaseConfig) {
        config.select(this)
                .setStrings("最大护盾值")
                .set("combat-power", 1)
                .superior()
                .select("block-damage")
                .setStrings("阻挡伤害")
                .set("combat-power", 1)
                .superior()["CancelEvent"] = true

    }

    override fun inject(entity: LivingEntity?, string: String, baseDoubles: Array<BaseDouble>) {
        baseDoubles[0].merge(baseConfig.analysis(this, string, ValueType.ALL))
        baseDoubles[1].merge(baseConfig.analysis("block-damage", string, ValueType.ALL))
    }

    override fun count(baseDoubles: Array<BaseDouble>): Long {
        return (baseDoubles[0].number() * baseConfig.select(this).any("combat-power").asNumber().toDouble() +
                baseDoubles[1].number() * baseConfig.select("block-damage").any("combat-power").asNumber().toDouble()
                ).roundToLong()
    }

    override fun format(entity: LivingEntity, s: String, baseDoubles: Array<BaseDouble>): Any {
        return if (s.equals("block", ignoreCase = true)){
            baseDoubles[1].number()
        } else{
            baseDoubles[0].number()
        }
    }

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        if (eventMemory is DamageMemory) {
            val damageMemory: DamageMemory = eventMemory as DamageMemory
            if (ShieldRecovery.map.containsKey(eventMemory.injured)){

                if (damageMemory.damage >= 0){
                    if (ShieldRecovery.map[eventMemory.injured]!! - damageMemory.damage >= 0){
                        ShieldRecovery.map[eventMemory.injured] = ShieldRecovery.map[eventMemory.injured]!! - damageMemory.damage
                        damageMemory.setLabel(Shield::class.java, baseDoubles[1].number()).addDamage(-baseDoubles[1].number())
                        if (damageMemory.damage <= 0 && baseConfig.any("CancelEvent").asBoolean()){
                            damageMemory.event.isCancelled = true
                        }
                    }else{
                        var damage: Double = ShieldRecovery.map[eventMemory.injured]!!
                        damageMemory.setLabel(Shield::class.java, damage).addDamage(-damage)
                        ShieldRecovery.map[eventMemory.injured] = 0.0
                    }

                }
            }
        }
    }


}