package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import ac.github.oa.internal.core.attribute.*
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.util.random

class Potion : AbstractAttribute() {

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.ATTACK)


    companion object {

        val instance: Potion
            get() = AttributeManager.getAttribute(Potion::class.java) as Potion

        @SubscribeEvent(ignoreCancelled = true, priority = EventPriority.MONITOR)
        fun e(e: EntityDamageEvent) {
            if (e.priorityEnum == PriorityEnum.POST) {
                instance.entries.forEach {
                    it as PotionAttribute
                    if (e.damageMemory.labels["@${it.name}"] == true) {
                        e.damageMemory.injured.addPotionEffect(it.createPotion())
                    }
                }
            }
        }

    }

    override fun loadEntry() {
        root.getKeys(false).forEach { key ->
            entries += PotionAttribute().also {
                it.name = key
                it.node = this
                it.index = entries.size
            }
        }
        entries.forEachIndexed { _, entry ->
            entry.onEnable()
        }
        info("|- Registered attribute ${toName()}.")
    }

    class PotionAttribute : Attribute.Entry() {

        val amplifier: List<Int>
            get() = getRoot().getIntegerList("${name}.amplifier")

        val duration: List<Int>
            get() = getRoot().getIntegerList("${name}.duration")

        val potionType: PotionEffectType
            get() = PotionEffectType.getByName(getRoot().getString("${name}.type")!!)!!

        fun createPotion(): PotionEffect {
            return PotionEffect(potionType, nextDuration(), nextAmplifier())
        }

        fun nextAmplifier(): Int {
            return when (amplifier.size) {
                1 -> amplifier[0]
                2 -> random(amplifier[0], amplifier[1])
                else -> amplifier.random()
            }
        }

        fun nextDuration(): Int {
            return when (duration.size) {
                1 -> duration[0]
                2 -> random(duration[0], duration[1])
                else -> duration.random()
            }
        }

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            if (memory is DamageMemory && random(data.get(0) / 100)) {
                memory.labels["@${this.name}"] = true
            }
        }

    }


}