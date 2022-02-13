package ac.github.oa.internal.core.item.generator

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.core.item.Item
import ac.github.oa.internal.core.item.Translator
import ac.github.oa.internal.core.script.hoop.MapScript
import ac.github.oa.util.random
import ac.github.oa.util.rebuild
import org.bukkit.Color
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.library.xseries.XEnchantment
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.ItemTagList
import taboolib.module.nms.getItemTag
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.buildItem
import java.util.*
import java.util.logging.Level

@Awake(LifeCycle.LOAD)
open class DefaultGenerator : ItemGenerator {

    override val name: String
        get() = ""

    override fun build(
        entity: LivingEntity?,
        item: Item,
        map: MutableMap<String, String>
    ): ItemStack {

        val config = item.config
        val wrapper = MapScript.Wrapper().apply { putAll(map) }

        config.getStringList("create-pre").forEach { it.random(wrapper, entity) }

        val id = config.getString("id")!!.random(wrapper, entity)
        val data = config.getString("data", "0")!!.random(wrapper, entity)
        val name = config.getString("name", "name null")!!.random(wrapper, entity)
        val lore = config.getStringList("lore").toMutableList().random(wrapper, entity).rebuild()
        val enchantments = config.getStringList("enchantments").toMutableList().random(wrapper, entity).rebuild()
        val itemFlags = config.getStringList("item-flags").toMutableList().random(wrapper, entity).rebuild()
        val unbreakable = config.getString("unbreakable", "false")!!.random(wrapper, entity)
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
                val key = XEnchantment.valueOf(split[0]).parseEnchantment()!!
                val level = if (split.size == 2) split[1].toInt() else key.startLevel
                enchants[key] = (enchants[key] ?: 0) + level
            }
            itemFlags.forEach {
                this.flags.add(ItemFlag.valueOf(it))
            }
            this.isUnbreakable = unbreakable.toBoolean()

            if (config.isString("skull-owner")) {
                skullOwner = config.getString("skull-owner")!!.random(wrapper, entity)
            }

            if (config.isString("skull-texture")) {
                XMaterial.PLAYER_HEAD
                skullTexture = ItemBuilder.SkullTexture(
                    config.getString("skull-texture")!!.random(wrapper, entity),
                    UUID.randomUUID()
                )
            }
            if (config.isString("color")) {
                color = config.getString("color")!!.random(wrapper, entity).run {
                    val split = this.split(",")
                    Color.fromRGB(split[0].toInt(), split[1].toInt(), split[2].toInt())
                }
            }

            colored()
        }.apply {
            val itemTag = getItemTag()
            val json = OriginAttribute.json.toJson(wrapper)
            itemTag["oa-session"] = ItemTagData(json)

            if (config.isConfigurationSection("nbt")) {
                config.getConfigurationSection("nbt")!!.getKeys(false).forEach {
                    itemTag[it] = Translator.toNBTBase(config["nbt.$it"])
                }
            }
            if (item.isClearDefault) {
                itemTag["AttributeModifiers"] = ItemTagData(ItemTagList())
            }

            itemTag.saveTo(this)
        }
    }


}