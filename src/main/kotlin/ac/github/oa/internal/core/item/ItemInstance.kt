package ac.github.oa.internal.core.item

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.core.attribute.equip.Slot
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag
import taboolib.platform.util.isAir

class ItemInstance(val itemStack: ItemStack, val item: Item, val slot: Slot? = null) {

    companion object {

        fun get(item: ItemStack): ItemInstance? {
            if (item.isAir()) return null
            val parseItem = ItemPlant.parseItem(item) ?: return null
            return ItemInstance(item, parseItem)
        }

    }

    fun rebuild(player: Player): ItemStack {
        return item.builder().create(player, itemStack.amount, getSession().toMutableMap()).first()
    }

    @Suppress("UNCHECKED_CAST")
    fun getSession(): Map<String, String> {
        return OriginAttribute.json.fromJson(
            itemStack.getItemTag()["oa-session"]?.asString(),
            Map::class.java
        ) as Map<String, String>
    }

}