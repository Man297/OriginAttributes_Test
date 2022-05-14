package ac.github.oa

import ac.github.oa.api.event.plugin.OriginPluginEnableEvent
import com.google.gson.Gson
import org.bukkit.Bukkit
import taboolib.common.platform.Platform
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.metrics.Metrics
import taboolib.platform.BukkitPlugin
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.function.Consumer


object OriginAttribute : Plugin() {

    @Config("config.yml", migrate = true)
    lateinit var config: Configuration

    @Config("module.yml", migrate = true)
    lateinit var module: Configuration

    val decimalFormat: DecimalFormat
        get() = DecimalFormat(config.getString("options.decimal-format"))

    val simpleDateFormat: SimpleDateFormat
        get() = SimpleDateFormat(config.getString("options.time-format")!!)

    val json = Gson()

    val numberRegex = Regex("^[+-]?[1-9]*[0-9]?(\\.[0-9]{1,2})?\$")

    override fun onLoad() {
        Metrics(12489, BukkitPlugin.getInstance().description.version, Platform.BUKKIT)
    }

    val original: Boolean
        get() = config.getBoolean("options.original")

    val skipStrings: List<String>
        get() = config.getStringList("options.skip-strings")


    override fun onEnable() {
        // Plugin startup logic
        OriginPluginEnableEvent().call()
        hookExecute("MythicMobs") {
            BukkitPlugin.getInstance().logger.info("|- MythicMobs plugin hooked.")
        }

    }


    fun hookExecute(name: String?, consumer: Consumer<Void?>) {
        if (Bukkit.getPluginManager().isPluginEnabled(name!!)) {
            consumer.accept(null)
        }
    }

    override fun onDisable() {
    }

}
