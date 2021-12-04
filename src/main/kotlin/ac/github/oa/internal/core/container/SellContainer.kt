package ac.github.oa.internal.core.container

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.core.item.impl.DefaultGenerator
import ac.github.oa.util.any
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.inventory.ItemStack
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.isNotAir

class SellContainer(val player: Player) {

    companion object {
        val builder = DefaultGenerator()


        val conf: ConfigurationSection
            get() = OriginAttribute.config.getConfigurationSection("options.sell")!!

        val keyword: List<String>
            get() = conf.getStringList("keyword")

    }


    fun open() {


        val templateButtons = getTemplateButtons()
        player.openMenu<Linked<ItemTemplateButton>>(title = conf.getString("title")!!) {

            rows(conf.getInt("rows", 6))

            templateButtons.forEach {
                it.slots.forEach { slot ->
                    set(slot, it.item) { it.handleClick(this) }
                }
            }

            onClick { event, element ->
                element.handleClick(event)
            }
        }

    }

    private fun getTemplateButtons(): ArrayList<ItemTemplateButton> {
        val list = arrayListOf<ItemTemplateButton>()
        val itemStack = builder.build(player, conf.getConfigurationSection("template-s")!!, mutableMapOf())
        list.add(SubmitTemplateButton(itemStack, conf.getIntegerList("slots") ?: arrayListOf()))
        conf.getKeys(false).filter { it.startsWith("template") && it.endsWith("s") }.forEach {
            list.add(
                ItemTemplateButton(
                    builder.build(player, conf.getConfigurationSection(it)!!, mutableMapOf()),
                    conf.getIntegerList("slots") ?: arrayListOf()
                )
            )
        }
        return list
    }

    class SubmitTemplateButton(item: ItemStack, slots: List<Int>) : ItemTemplateButton(item, slots) {
        override fun handleClick(event: ClickEvent) {

            // 双击出售
            if (event.clickEvent().click === ClickType.DOUBLE_CLICK) {
                val slots = conf.getIntegerList("slots")
                val inventory = event.inventory
                var count = 0
                val map = slots.map { SellItem(inventory.getItem(it), it) }.filter { it.check() }.filter {
                    any(it.itemStack!!.itemMeta!!.lore!!, keyword)
                    // TODO sell ...
                    true
                }

            }

        }
    }

    class SellItem(val itemStack: ItemStack?, index: Int) {
        fun check() = itemStack != null && itemStack.hasItemMeta() && itemStack.itemMeta!!.hasLore()
    }

    open class ItemTemplateButton(override val item: ItemStack, val slots: List<Int>) : TemplateButton {
        override fun handleClick(event: ClickEvent) {


        }
    }

    interface TemplateButton {
        fun handleClick(event: ClickEvent)

        val item: ItemStack

    }


}