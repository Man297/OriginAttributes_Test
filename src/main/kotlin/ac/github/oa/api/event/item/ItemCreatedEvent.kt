package ac.github.oa.api.event.item
//
import ac.github.oa.internal.core.item.Item
import org.bukkit.entity.LivingEntity
import ac.github.oa.internal.core.item.generator.ItemGenerator
import org.bukkit.inventory.ItemStack
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.nms.ItemTag
import taboolib.platform.type.BukkitProxyEvent

class ItemCreatedEvent(
    var entity: LivingEntity?,
    var item: Item,
    var itemStack: ItemStack,
    var generator: ItemGenerator
) : BukkitProxyEvent()