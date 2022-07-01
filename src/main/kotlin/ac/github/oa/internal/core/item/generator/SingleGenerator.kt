package ac.github.oa.internal.core.item.generator

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.core.item.Item
import ac.github.oa.internal.core.item.ItemPlant
import ac.github.oa.util.rebuild
import org.bukkit.Color
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.Coerce
import taboolib.library.xseries.XEnchantment
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.buildItem
import taboolib.platform.util.isAir
import java.util.*
import java.util.logging.Level
import kotlin.math.ceil

@Awake(LifeCycle.LOAD)
class SingleGenerator : ItemGenerator {

    companion object {

        val appendLore: List<String>
            get() = OriginAttribute.config.getStringList("options.generator.single.append")

        @SubscribeEvent
        fun e(e: PlayerInteractEvent) {
            if (e.hand === EquipmentSlot.HAND && e.action == Action.RIGHT_CLICK_AIR) {
                val player = e.player
                val item = e.item ?: return
                if (item.isAir()) return
                val itemInstance = ItemPlant.parseItem(item) ?: return
                val config = itemInstance.config
                val stringList = config.getStringList("group").map { it.split(" ") }
                val list = stringList.mapNotNull {
                    val key = it[0]
                    val amount = Coerce.toInteger(it.getOrElse(1) { "1" })
                    ItemPlant.build(player, key, mutableMapOf())?.apply {
                        this.amount = amount
                        val itemMeta = itemMeta
                        val lore = itemMeta!!.lore ?: arrayListOf()
                        lore.addAll(appendLore.map { it.replace("&", "§") })
                        itemMeta.lore = lore
                        this.itemMeta = itemMeta
                    }
                }
                if (list.isEmpty()) return

                player.openSingleChest(item.itemMeta?.displayName ?: "__null__", list) { clickEvent, i ->
                    val strings = stringList[i]
                    item.amount = item.amount - 1
                    val integer = Coerce.toInteger(strings.getOrElse(1) { "1" })
                    (0 until integer).forEach { _ ->
                        ItemPlant.build(player, strings[0])?.let { result ->
                            player.inventory.addItem(result)
                        }
                    }
                }
            }
        }

        fun Player.openSingleChest(title: String, list: List<ItemStack>, callback: (ClickEvent, Int) -> Unit) {
            val rows = ceil(list.size / 9.0).toInt()
            openMenu<Linked<ItemStack>>(title) {
                rows(rows)
                elements { list }
                list.forEachIndexed { index, itemStack ->
                    set(index, itemStack) {
                        closeInventory()
                        callback(this, index)
                    }
                }

                onBuild {
                    world.playSound(location, Sound.BLOCK_CHEST_OPEN, 1f, 1f)
                }

                onClose {
                    world.playSound(location, Sound.BLOCK_CHEST_OPEN, 1f, 1f)
                }
            }
        }

    }


    override val name: String
        get() = "single"

    override fun build(
        entity: LivingEntity?,
        item: Item,
        map: MutableMap<String, String>
    ): ItemStack {
        val config = item.config
        val id = config.getString("id")!!
        val data = config.getString("data", "0")!!
        val name = config.getString("name", "name null")!!
        val lore = config.getStringList("lore").toMutableList().rebuild()
        val enchantments = config.getStringList("enchantments").toMutableList().rebuild()
        val itemFlags = config.getStringList("item-flags").toMutableList().rebuild()
        val unbreakable = config.getString("unbreakable", "false")!!
        val optional = XMaterial.matchXMaterial(id)

        val material = if (optional.isPresent) {
            optional.get()
        } else {
            BukkitPlugin.getInstance().logger.log(Level.WARNING, "无效的id $id,追踪节点 ${config.name}.")
            XMaterial.AIR
        }
        return buildItem(material) {
            this.damage = data.toInt()
            this.name = name
            this.lore.addAll(lore)
            enchantments.forEach {
                val split = it.split(":")
                val key = XEnchantment.matchXEnchantment(split[0]).get().enchant!!
                val level = if (split.size == 2) split[1].toInt() else key.startLevel
                enchants[key] = (enchants[key] ?: 0) + level
            }
            itemFlags.forEach {
                this.flags.add(ItemFlag.valueOf(it))
            }
            this.isUnbreakable = unbreakable.toBoolean()

            if (config.isString("skull-owner")) {
                skullOwner = config.getString("skull-owner")!!
            }

            if (config.isString("skull-texture")) {
                XMaterial.PLAYER_HEAD
                skullTexture = ItemBuilder.SkullTexture(
                    config.getString("skull-texture")!!,
                    UUID.randomUUID()
                )
            }
            if (config.isString("color")) {
                color = config.getString("color")!!.run {
                    val split = this.split(",")
                    Color.fromRGB(split[0].toInt(), split[1].toInt(), split[2].toInt())
                }
            }
            colored()
        }
    }


}