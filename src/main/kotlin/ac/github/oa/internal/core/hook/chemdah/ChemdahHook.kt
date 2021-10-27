package ac.github.oa.internal.core.hook.chemdah

import ink.ptms.chemdah.core.quest.QuestLoader.register
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object ChemdahHook {

    @Awake(LifeCycle.ENABLE)
    fun e() {
        if (Bukkit.getPluginManager().isPluginEnabled("Chemdah")) {
            OriginItemGet.register()
        }
    }

}