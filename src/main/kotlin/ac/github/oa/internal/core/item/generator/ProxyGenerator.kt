package ac.github.oa.internal.core.item.generator

import ac.github.oa.internal.core.item.Item
import ac.github.oa.internal.core.item.ItemPlant
import ac.github.oa.internal.core.item.script.hoop.MapScript
import ac.github.oa.util.random
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.util.random
import taboolib.common5.Coerce
import taboolib.module.configuration.util.getMap

@Awake(LifeCycle.LOAD)
class ProxyGenerator : ItemGenerator {

    override val name: String
        get() = "proxy"

    private fun Item.vars(): Map<String, Any> {
        return config.getMap("vars")
    }

    private fun Item.id(): String {
        return config.getString("id")!!
    }

    override fun build(entity: LivingEntity?, item: Item, map: MutableMap<String, String>): ItemStack {
        map.putAll(item.vars().map { it.key to it.value.random(MapScript.Wrapper(), entity) })
        val id = item.id()
        return ItemPlant.build(entity, id, map) ?: error("Item $id not found.")
    }
}