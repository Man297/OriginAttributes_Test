package ac.github.oa.internal.core.ui

import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked

class InfoUI(val viewer: Player, val target: Player) {

    fun open() {

        viewer.openMenu<Linked<Void>>(Info.toTitle(viewer, target)) {
            rows(Info.rows)

            Info.icons.forEach {
                it.slots.forEach { slot ->
                    set(slot, it.toIcon(target))
                }
            }

        }


    }

}