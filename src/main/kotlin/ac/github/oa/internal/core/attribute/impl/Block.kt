package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import ac.github.oa.internal.core.attribute.*
import org.bukkit.Sound
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.util.random
import taboolib.common5.Coerce

class Block : AbstractAttribute() {

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.DEFENSE)


    companion object {

        val blockInstance: Block
            get() = AttributeManager.getAttribute("Block") as Block

        val chanceEntry: ChanceImpl
            get() = blockInstance.chance

        @SubscribeEvent
        fun e(e: EntityDamageEvent) {
            if (e.priorityEnum == PriorityEnum.POST && e.damageMemory.labels["@Block"] == true) {
                val injured = e.damageMemory.injured
                e.isCancelled = true
                e.damageMemory.event.isCancelled = true
                injured.world.playSound(injured.eyeLocation, chanceEntry.sound, chanceEntry.volume, chanceEntry.pitch)
            }
        }

    }


    val chance = ChanceImpl()

    val ratio = object : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE

        val default: Double
            get() = node.toRoot().getDouble("${name}.default", 0.0)

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            if (memory.labels["@Block"] == true) {
                val percent = (data.get(this) + default) / 100
                var count = 0.0
                memory.getDamageSources().forEach {
                    it.value -= (it.value * percent).apply { count += this }
                }
                memory.labels["block-ratio"] = count
            }
        }
    }

    val damage = object : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.RANGE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            if (memory.labels["@Block"] == true) {
                val damage = data.get(this)
                var count = 0.0
                memory.getDamageSources().forEach {
                    it.value -= (it.value * damage).apply { count += this }
                }
                memory.labels["block-damage"] = count
            }
        }
    }

    class ChanceImpl : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE

        val volume: Float
            get() = Coerce.toFloat(node.toRoot()["${name}.volume"])

        val pitch: Float
            get() = Coerce.toFloat(node.toRoot()["${name}.pitch"])

        val sound: Sound
            get() = node.toRoot().getEnum("${name}.sound", Sound::class.java)!!

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            memory.setLabel("@Block", random(data.get(this)))
        }
    }

}
