package ac.github.oa.internal.attribute.impl.attack

import ac.github.oa.OriginAttribute
import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.internal.attribute.AttributeData
import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.attribute.impl.attack.Hit
import ac.github.oa.internal.attribute.impl.defense.Armor
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerExpChangeEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.BukkitPlugin
import kotlin.math.roundToLong

class RangeDamage : SingleAttributeAdapter(AttributeType.ATTACK) {

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        val damageMemory: DamageMemory = eventMemory as DamageMemory;
        val range: Double = baseDoubles[0].number()

        if (range >= 1)
        for (entity in damageMemory.attacker.getNearbyEntities(range, range, range)){
            if (entity is LivingEntity){
                Bukkit.getScheduler().runTask(BukkitPlugin.getInstance(), damage(entity, damageMemory.damage, damageMemory.attacker))
            }
        }
    }

    override fun format(entity: LivingEntity, s: String, baseDoubles: Array<BaseDouble>): Any {
        return baseDoubles[0].number()
    }
    override fun count(baseDoubles: Array<BaseDouble>): Long {
        return (baseDoubles[0].number() * baseConfig.select(this).any("combat-power").asNumber().toDouble()).roundToLong()
    }

    override val strings: Array<String>
        get() = arrayOf("群体攻击范围")
    override val type: ValueType
        get() = ValueType.NUMBER

    class damage(var entity: LivingEntity, var damage: Double, var attacker: LivingEntity): Runnable{
        override fun run() {
            entity!!.damage(damage, attacker);
        }
    }
}