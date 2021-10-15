package ac.github.oa.internal.attribute.impl.defense


import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.attribute.AttributeData
import ac.github.oa.internal.attribute.AttributeType

import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import kotlin.math.roundToLong

class Armor : AttributeAdapter(3, AttributeType.DEFENSE) {
    override fun defaultOption(config: BaseConfig) {
        config.select(this)
                .setStrings("防御力")
                .set("scale", 0.8)
                .set("combat-power",1)
                .superior()
                .select("armor-pvp")
                .setStrings("对玩家防御")
                .set("combat-power",1)
                .superior()
                .select("armor-pve")
                .set("combat-power",1)
                .setStrings("对怪物防御")
                .superior()["CancelEvent"] = true
    }

    override fun count(baseDoubles: Array<BaseDouble>): Long {
        return (baseDoubles[0].number() * baseConfig.select(this).any("combat-power").asNumber().toDouble() +
                baseDoubles[1].number() * baseConfig.select("armor-pvp").any("combat-power").asNumber().toDouble() +
                baseDoubles[2].number() * baseConfig.select("armor-pve").any("combat-power").asNumber()
                .toDouble()).roundToLong()
    }

    override fun inject(entity: LivingEntity?, string: String, baseDoubles: Array<BaseDouble>) {
        baseDoubles[0].merge(baseConfig.analysis(this, string, ValueType.NUMBER))
        baseDoubles[1].merge(baseConfig.analysis("armor-pvp", string, ValueType.PERCENT))
        baseDoubles[2].merge(baseConfig.analysis("armor-pve", string, ValueType.PERCENT))
    }

    override fun format(entity: LivingEntity, s: String, baseDoubles: Array<BaseDouble>): Any {
        return when (s) {
            "pvp" -> baseDoubles[1].number()
            "pve" -> baseDoubles[2].number()
            else -> baseDoubles[0].number()
        }
    }
    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        if (eventMemory is DamageMemory) {
            val damageMemory: DamageMemory = eventMemory as DamageMemory
            val baseDouble: BaseDouble = baseDoubles[0]
            val scale: Double = baseConfig.select(this).any("scale").asNumber().toDouble()
            var result: Double = baseDouble.number() * scale
            val injured: LivingEntity = damageMemory.injured
            result += if (injured is Player) {
                baseDoubles[1].eval(damageMemory.damage)
            } else {
                baseDoubles[2].eval(damageMemory.damage)
            }
            damageMemory.setLabel(Armor::class.java, result).addDamage(-result)
            if (damageMemory.damage <= 0 && baseConfig.any("CancelEvent").asBoolean()){
                damageMemory.event.isCancelled = true
            }
        }
    }
}