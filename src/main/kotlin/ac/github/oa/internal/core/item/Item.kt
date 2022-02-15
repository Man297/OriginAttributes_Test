package ac.github.oa.internal.core.item

import ac.github.oa.internal.core.item.action.IActionEvent
import ac.github.oa.internal.core.item.action.ItemAction
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.library.configuration.ConfigurationSection

class Item(val config: ConfigurationSection) {

    // id
    val key = config.name

    // 是否开启动态更新
    val isUpdate = config.getBoolean("update", false)

    // 标注版本
    val hasCode = config.getValues(false).hashCode()

    // 生成器
    val generator = config.getString("g", "")!!

    val isClearDefault = config.getBoolean("clear-default")

    val actions = config.getMapList("actions").map {
        val list = when (val any = it["action"] ?: listOf<String>()) {
            is String -> listOf(any.toString())
            is List<*> -> any.map { it.toString() }
            else -> listOf()
        }
        ItemAction(it["\$e"].toString(), list)
    }

    // 创建
    fun create(entity: LivingEntity?, options: MutableMap<String, String> = mutableMapOf()): ItemStack? {
        return ItemPlant.build(entity, key, options)
    }


    fun builder() = ItemBuilder(this)

}