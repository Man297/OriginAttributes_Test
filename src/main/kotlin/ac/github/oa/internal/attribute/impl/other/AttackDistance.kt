package ac.github.oa.internal.attribute.impl.other

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.util.isEnabled
import org.bukkit.Particle
import org.bukkit.entity.LivingEntity
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import taboolib.platform.BukkitPlugin

class AttackDistance : SingleAttributeAdapter(AttributeType.OTHER) {

    companion object {
        @SubscribeEvent
        fun e(e: PlayerInteractEvent) {
            if (AttackDistance::class.java.isEnabled() && e.action === Action.LEFT_CLICK_AIR && e.hand === EquipmentSlot.HAND) {
                val player = e.player
                val attributeData = OriginAttributeAPI.getAttributeData(player)
                val arrayOfBaseDoubles = attributeData.find(AttackDistance::class.java)
                val number = Coerce.toInteger(arrayOfBaseDoubles[0].number())
                val location = player.location.clone()
                IntRange(0, number).forEach { it ->
                    // 步进
                    location.add(location.direction.multiply(2.5 + it))
                    // 获取周围1格内的实体
                    val list = player.world.getNearbyEntities(location, 1.0, 1.0, 1.0).filterIsInstance<LivingEntity>()

                    // 获取非自身实体的第一个
                    list.firstOrNull { it != player }?.let {
                        it.damage(0.0, player)
                        return@forEach
                    }
                }
            }
        }
    }

    override val strings: Array<String>
        get() = arrayOf("攻击距离")
    override val type: ValueType
        get() = ValueType.NUMBER

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {

    }
}