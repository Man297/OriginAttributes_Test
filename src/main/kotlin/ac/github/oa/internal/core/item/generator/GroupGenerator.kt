package ac.github.oa.internal.core.item.generator

import ac.github.oa.internal.core.item.Item
import ac.github.oa.internal.core.item.ItemPlant
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.util.random
import taboolib.common5.Coerce

@Awake(LifeCycle.LOAD)
class GroupGenerator : ItemGenerator {
    override val name: String
        get() = "group"

    fun Item.items(): List<String> {
        return config.getStringList("items")
    }

    fun Item.firstItem(): Pair<String, Int> {
        val items = items()
        val string = items.random()
        val split = string.split(" ")
        if (random(Coerce.toDouble(split[2]))) {
            return Pair(split[0], Coerce.toInteger(split[1]))
        }
        return firstItem()
    }

    override fun build(entity: LivingEntity?, item: Item, map: MutableMap<String, String>): ItemStack {
        val pair = item.firstItem()
        return ItemPlant.build(entity, pair.first, map)?.also {
            it.amount = pair.second
        } ?: error("Item ${pair.first} not found.")
    }
}