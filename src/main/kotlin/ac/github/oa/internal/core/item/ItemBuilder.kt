package ac.github.oa.internal.core.item

import ac.github.oa.api.event.entity.EntityGetItemEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial

class ItemBuilder(val item: Item) {

    fun create(
        target: LivingEntity?,
        amount: Int = 1,
        options: MutableMap<String, String> = mutableMapOf()
    ): MutableList<ItemStack> {
        return (0 until amount).mapNotNull {
            item.create(target, options)
        }.toMutableList()
    }

    fun to(target: Player, amount: Int = 1, options: MutableMap<String, String> = mutableMapOf()): List<ItemStack> {
        return to(target, create(target, amount, options))
    }

    fun to(target: Player, itemStack: MutableList<ItemStack>): List<ItemStack> {
        val event = EntityGetItemEvent(target, itemStack, item).apply { call() }
        if (event.isCancelled) return mutableListOf()
        event.result.forEach {
            if (target.inventory.firstEmpty() == -1) {
                target.world.dropItem(target.location, it)
            } else {
                target.inventory.addItem(it)
            }
        }
        return event.result
    }

}