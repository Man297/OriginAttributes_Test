package ac.github.oa.internal.core.equip

import org.bukkit.entity.LivingEntity

interface SlotVariation {

    fun examine(livingEntity: LivingEntity, adaptItem: AdaptItem, patterns: List<String>): Boolean
}