package ac.github.oa.api.event.item
//
import org.bukkit.entity.LivingEntity
import ac.github.oa.internal.core.item.ItemGenerator
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.ProxyEvent
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.nms.ItemTag

class ItemCreatedEvent(
    var entity: LivingEntity?,
    var config: ConfigurationSection,
    var itemStack: ItemStack,
    var generator: ItemGenerator,
    var itemTag: ItemTag
) : ProxyEvent()