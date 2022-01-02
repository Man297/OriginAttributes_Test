package ac.github.oa.internal.core.hook

import ac.github.oa.internal.core.item.ItemPlant
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill
import io.lumine.xikage.mythicmobs.skills.SkillMechanic
import io.lumine.xikage.mythicmobs.skills.SkillMetadata
import io.lumine.xikage.mythicmobs.skills.mechanics.CustomMechanic
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderInt
import taboolib.common.platform.event.SubscribeEvent

class MythicMobDropOriginItem(
    holder: CustomMechanic, mlc: MythicLineConfig
) : SkillMechanic(holder.configLine, mlc), ITargetedEntitySkill {

    companion object {
        @SubscribeEvent
        fun e(e: MythicMechanicLoadEvent) {
            if (e.mechanicName == "oa:dropItem") {
                e.register(MythicMobDropOriginItem(e.container, e.config))
            }
        }
    }

    private var amount: PlaceholderInt = mlc.getPlaceholderInteger("amount", "a")
    val key: String = mlc.getString("key")

    override fun castAtEntity(skillMetadata: SkillMetadata, abstractEntity: AbstractEntity): Boolean {

        val itemStacks = IntRange(0, amount.get()).map { ItemPlant.build(null, key) }
        val entity = BukkitAdapter.adapt(abstractEntity)
        itemStacks.filterNotNull().forEach { entity.world.dropItem(entity.location, it) }
        return itemStacks.isNotEmpty()
    }
}