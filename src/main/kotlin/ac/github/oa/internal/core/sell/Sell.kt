package ac.github.oa.internal.core.sell

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.core.item.ItemPlant
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.buildMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.buildItem

class Sell(player: Player) {

    init {
        val config = OriginAttribute.config.getConfigurationSection("options.sell")!!
        val layout = config.getStringList("layout")!!
        buildMenu<Basic>(title = config.getString("title")!!) {
            rows(layout.size)
            map(*layout.toTypedArray())
            config.getKeys(false).forEach {
                if (it.startsWith("template")) {
                    val char = it.split("-")[0]
                    set(char.toCharArray()[0], ItemPlant.build(null, config.getString(it)!!) ?: buildItem(XMaterial.AIR))
                }
            }

            onClick {
                if (it.slot != 'i') {
                    it.isCancelled = true
                }
                if (it.slot == 's') {
                    val inventory = it.inventory
                    config.getIntegerList("slots").forEach {
                        val item = inventory.getItem(it)
                        val flag = 0 // 0 = 失败

                    }
                }
            }

        }
    }

}