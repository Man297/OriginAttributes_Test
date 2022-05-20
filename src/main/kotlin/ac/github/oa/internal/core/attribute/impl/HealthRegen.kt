package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.core.attribute.*
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import kotlin.math.max
import kotlin.math.min

class HealthRegen : AbstractAttribute() {

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.OTHER)

    private val period: Long
        get() = toRoot().getLong("__option__.period")

    override fun onLoad() {
        super.onLoad()
        submit(async = true, period = this.period) {
            Bukkit.getOnlinePlayers().forEach {
                if (it.health != it.maxHealth) {

                    val regenValue = it.getRegenValue()
                    if (regenValue > 0) {
                        val result = min(regenValue + it.health, it.maxHealth)
                        it.health = result
                    }
                }
            }
        }
    }

    fun LivingEntity.getRegenValue(): Double {
        return OriginAttributeAPI.getAttributeData(this).getRegenValue(this)
    }

    fun AttributeData.getRegenValue(entity: LivingEntity): Double {
        var count = getData(this@HealthRegen.index, amount.index).get(amount)
        count += entity.health * (getData(this@HealthRegen.index, ratio.index).get(ratio) / 100)
        return count
    }

    val amount = object : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.RANGE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {

        }

    }

    val ratio = object : Attribute.Entry() {
        override fun handler(memory: EventMemory, data: AttributeData.Data) {

        }
    }

}
