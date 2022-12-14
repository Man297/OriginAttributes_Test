package ac.github.oa.internal.core.item.random

import ac.github.oa.internal.core.item.script.InternalScript
import ac.github.oa.internal.core.item.script.hoop.MapScript
import ac.github.oa.util.listFile
import ac.github.oa.util.newfolder
import org.bukkit.ChatColor
import org.bukkit.entity.LivingEntity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.module.configuration.SecuredFile
import taboolib.platform.BukkitPlugin
import java.util.logging.Level

object RandomPlant {

    val configs = mutableMapOf<String, Any>()

    val folder = newfolder(BukkitPlugin.getInstance().dataFolder, "random", listOf("default.yml"))


    @Awake(LifeCycle.ENABLE)
    fun init() {
        configs.clear()
        folder.listFile("yml").forEach {
            SecuredFile.loadConfiguration(it).apply {
                getKeys(true).forEach {
                    if (this.isString(it)) {

                        configs[ChatColor.stripColor(it).toString()] = getString(it)!!
                    } else if (this.isList(it)) {
                        configs[ChatColor.stripColor(it).toString()] = getStringList(it)
                    }
                }
            }
        }
        BukkitPlugin.getInstance().logger.log(Level.INFO, "|- 共加载随机 ${configs.size} 个配置")
    }

    fun eval(string: String, entity: LivingEntity?, wrapper: MapScript.Wrapper): String {
        return InternalScript.transform(string, entity, listOf(wrapper)) { internalConfig, s ->
            var result = s
            internalConfig.id?.apply {
                if (!wrapper.containsKey(this)) {
                    wrapper[this] = s
                } else {
                    result = wrapper[this].toString()
                }
            }
            result
        }
    }

}