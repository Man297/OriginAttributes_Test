package ac.github.oa.internal.item

import ac.github.oa.api.event.item.ItemCreatedEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.getItemTag

object ItemPlant {

    val generators = arrayListOf<ItemGenerator>()
    val configs = arrayListOf<ConfigurationSection>()


    fun hasKey(key: String): Boolean = configs.any { it.name == key }

    fun build(entity: LivingEntity?, key: String): ItemStack = build(entity, configs.first { it.name == key })

    fun build(entity: LivingEntity?, config: ConfigurationSection): ItemStack {
        val generatorKey = config.getString("g", "")
        val generator = generators.first { generatorKey == it.name }
        return generator.build(entity, config).apply {
            // 写入nbt
            val itemTag = getItemTag()
            itemTag["oa-key"] = ItemTagData(config.name)

            val event = ItemCreatedEvent(entity, config, this, generator, itemTag)
            event.call()

            event.itemTag.saveTo(event.itemStack)
        }
    }


}