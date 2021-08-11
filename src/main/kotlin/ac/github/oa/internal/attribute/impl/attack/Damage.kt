package ac.github.oa.internal.attribute.impl.attack

import ac.github.oa.internal.attribute.AttributeAdapter

import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.event.EventMemory
import ac.github.oa.internal.event.impl.DamageMemory
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

/**
 * 0 攻击伤害
 * 1 对玩家增伤
 * 2 对怪物增伤
 */
class Damage : AttributeAdapter(3) {
    override fun defaultOption(config: BaseConfig) {
        config.select(this)
            .setStrings("攻击力")
            .superior()
            .select("damage-pvp")
            .setStrings("对玩家增伤")
            .superior()
            .select("damage-pve")
            .setStrings("对怪物增伤")
    }

     override fun inject(entity: LivingEntity?, string: String, baseDoubles: Array<BaseDouble>) {
        baseDoubles[0].merge(baseConfig.analysis(this, string, ValueType.NUMBER))
        baseDoubles[1].merge(baseConfig.analysis("damage-pvp", string, ValueType.PERCENT))
        baseDoubles[2].merge(baseConfig.analysis("damage-pve", string, ValueType.PERCENT))
    }

    override fun format(entity: LivingEntity, s: String, baseDoubles: Array<BaseDouble>): Any  {
        return when (s) {
            "pvp" -> baseDoubles[1].number()
            "pve" -> baseDoubles[2].number()
            else -> baseDoubles[0].number()
        }
    }

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        if (eventMemory is DamageMemory) {
            val damageMemory: DamageMemory = eventMemory
            val baseDouble: BaseDouble = baseDoubles[0]
            damageMemory.addDamage("damage", baseDouble.number())
            val injured: LivingEntity = damageMemory.injured
            if (injured is Player) {
                damageMemory.addDamage("damage-pvp", baseDoubles[1].eval(damageMemory.damage))
            } else {
                damageMemory.addDamage("damage-pve", baseDoubles[2].eval(damageMemory.damage))
            }
        }
    }
}