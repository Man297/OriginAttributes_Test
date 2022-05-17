package ac.github.oa.internal.core.module

import ac.github.oa.OriginAttribute
import ac.github.oa.api.event.plugin.OriginPluginReloadEvent
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.library.configuration.ConfigurationSection
import taboolib.platform.util.sendActionBar

object ActionBarModule {


    val root: ConfigurationSection
        get() = OriginAttribute.module.getConfigurationSection("action-bar")
            ?: OriginAttribute.module.createSection("action-bar")

    val isEnable: Boolean
        get() = root.getBoolean("enable", false) && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")

    val period: Long
        get() = root.getLong("period")

    val value: String
        get() = root.getString("value", "")!!

    var timer: PlatformExecutor.PlatformTask? = null

    @Awake(LifeCycle.ENABLE)
    fun runTimer() {
        timer = null
        if (isEnable) {
            timer = submit(period = period) {
                Bukkit.getOnlinePlayers().forEach {
                    it.sendActionBar(PlaceholderAPI.setPlaceholders(it, value))
                }
            }
        }
    }

    @SubscribeEvent
    fun e(e: OriginPluginReloadEvent) {
        this.runTimer()
    }


}