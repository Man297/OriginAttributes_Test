package ac.github.oa.api

import ac.github.oa.OriginAttribute
import ac.github.oa.api.event.entity.OriginCustomDamageEvent
import ac.github.oa.api.event.item.ItemDurabilityDamageEvent
import ac.github.oa.api.event.item.ItemUpdateEvent
import ac.github.oa.internal.core.attribute.AttributeManager
import ac.github.oa.internal.core.attribute.equip.Hand
import ac.github.oa.internal.core.attribute.equip.OffHand
import ac.github.oa.internal.core.item.ItemPlant
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.getItemTag
import taboolib.platform.util.isAir
import kotlin.math.max
import kotlin.math.min

object ItemAPI {

    private val durabilityChar: Char
        get() = OriginAttribute.config.getString("options.durability-char", "/")!!.toCharArray()[0]

    private val durabilityKeyword: String
        get() = OriginAttribute.config.getString("options.durability")!!

    /**
     * 保留旧耐久
     */
    @SubscribeEvent
    fun e(e: ItemUpdateEvent) {
        val newItemStack = e.newItemStack
        val oldItemStack = e.oldItemStack

        // 如果武器存在耐久节点
        if (e.item.config.contains("nbt.${NBT.MAX_DURABILITY.key}")) {
            val max = e.item.config.getInt("nbt.${NBT.MAX_DURABILITY.key}")
            val durability = getDurability(oldItemStack)
            setDurability(newItemStack, durability)
            ItemDurabilityDamageEvent(e.player, newItemStack, max, max, durability).call()
        }
    }

//    @SubscribeEvent
//    fun e0(e: ItemDurabilityDamageEvent) {
//        val itemStack = e.itemStack
//        val durability = getDurability(itemStack)
//        if (durability <= 0) {
//            ItemPlant.parseItem(itemStack) ?: return
//            itemStack.amount = 0
//        }
//    }

    @SubscribeEvent(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun e(e: OriginCustomDamageEvent) {
        if (e.isCancelled) return
        // 扣除攻击者的主手副手耐久
        (e.damager as? Player)?.let { player ->

            val attributeData = AttributeManager.get(player)
            attributeData.items.forEach {
                if (it.enable && (it.slot is Hand || it.slot is OffHand)) {
                    it.item.takeDurability(player, 1)
                }
            }
        }
        // 扣除被攻击者的身体物品耐久
        (e.entity as? Player)?.let { player ->
            val attributeData = AttributeManager.get(player)
            attributeData.items.forEach {
                if (it.enable && (it.slot !is Hand)) {
                    it.item.takeDurability(player, 1)
                }
            }
        }
    }


    @SubscribeEvent
    fun e(e: ItemDurabilityDamageEvent) {
        val itemStack = e.itemStack
        val item = ItemPlant.parseItem(itemStack) ?: return
        if (item.config.contains("nbt.${NBT.MAX_DURABILITY.key}")) {
            var changed = false
            val itemMeta = itemStack.itemMeta ?: return
            val lore = itemMeta.lore?.toMutableList() ?: mutableListOf()
            lore.forEachIndexed { index, s ->
                if (s.contains(durabilityKeyword) && s.contains(durabilityChar)) {
                    changed = true
                    val split = s.split(durabilityChar).toMutableList()
                    split[0] = split[0].replace(getNumber(split[0]).toInt().toString(), e.durability.toString())
                    split[1] = split[1].replace(getNumber(split[1]).toInt().toString(), e.maxDurability.toString())
                    lore[index] = split.joinToString(durabilityChar.toString())
                }
            }
            if (changed) {
                itemMeta.lore = lore
                itemStack.itemMeta = itemMeta
            }
        }
    }

    fun getNumber(lore: String): Double {
        val str = lore.replace("#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})".toRegex(), "").replace("§+[a-z0-9]".toRegex(), "")
            .replace("[^-0-9.]".toRegex(), "")
        return if (str.isEmpty() || str.replace("[^.]".toRegex(), "").length > 1) 0.0 else str.toDouble()
    }

    fun checkUpdate(player: Player, inventory: Inventory) {
        (0 until inventory.size).forEach { i ->
            val item = inventory.getItem(i)
            if (item.isAir()) {
                return@forEach
            }
            val canUpdate = item!!.canUpdate()
            if (canUpdate) {
                val parseItem = ItemPlant.parseItem(item)!!
                val itemStack = parseItem.create(player)!!
                val event = ItemUpdateEvent(player, item, itemStack, parseItem)
                event.call()
                inventory.setItem(i, event.newItemStack)
            }
        }
    }

    fun ItemStack.canUpdate(): Boolean {
        val item = ItemPlant.parseItem(this) ?: return false
        if (!item.isUpdate) return false
        val itemTag = this.getItemTag()
        val asLong = itemTag["oa-hasCode"]?.asInt() ?: 0
        return asLong != item.hasCode
    }


    fun ItemStack.takeDurability(player: Player? = null, value: Int = 1) {
        if (this.type == Material.AIR) return
        val itemTag = getItemTag()
        if (itemTag.containsKey(NBT.MAX_DURABILITY.key)) {
            val maxDurability = itemTag[NBT.MAX_DURABILITY.key]!!.asInt()
            val oldDurability = itemTag[NBT.DURABILITY.key]?.asInt() ?: maxDurability
            val durability = oldDurability - value
            itemTag[NBT.DURABILITY.key] = ItemTagData(durability)
            itemTag.saveTo(this)
            if (player !== null) {
                ItemDurabilityDamageEvent(player, this, maxDurability, oldDurability, durability).call()
            }
            this.updateDurability(maxDurability, durability)
        }
    }

    fun getMaxDurability(itemStack: ItemStack): Int {
        val itemTag = itemStack.getItemTag()
        return itemTag[NBT.MAX_DURABILITY.key]?.asInt() ?: -1;
    }

    fun getDurability(itemStack: ItemStack): Int {
        val itemTag = itemStack.getItemTag()
        if (itemTag.containsKey(NBT.MAX_DURABILITY.key)) {
            val maxDurability = itemTag[NBT.MAX_DURABILITY.key]!!.asInt()
            return (itemTag[NBT.DURABILITY.key]?.asInt() ?: maxDurability)
        }
        return -1;
    }

    fun setDurability(itemStack: ItemStack, value: Int) {
        val itemTag = itemStack.getItemTag()
        if (itemTag.containsKey(NBT.MAX_DURABILITY.key)) {
            val maxDurability = itemTag[NBT.MAX_DURABILITY.key]!!.asInt()
            val min = min(max(value, 0), maxDurability)
            itemTag[NBT.DURABILITY.key] = ItemTagData(min)
            itemTag.saveTo(itemStack)
            itemStack.updateDurability(maxDurability, min)
        }
    }

    fun ItemStack.updateDurability() {
        val itemTag = getItemTag()
        if (itemTag.containsKey(NBT.MAX_DURABILITY.key)) {
            val maxDurability = itemTag[NBT.MAX_DURABILITY.key]!!.asInt()
            val durability = (itemTag[NBT.DURABILITY.key]?.asInt() ?: maxDurability)
            updateDurability(maxDurability, durability)
        }
    }

    private fun ItemStack.updateDurability(max: Int, current: Int) {
        val percent = current.toDouble() / max.toDouble()
        val durability = type.maxDurability
        this.durability = (durability - (durability * percent)).toInt().toShort()
    }


}
