package ac.github.oa

import ac.github.oa.api.event.plugin.OriginPluginEnableEvent
import ac.github.oa.internal.attribute.AttributeManager
import ac.github.oa.internal.attribute.impl.attack.*
import ac.github.oa.internal.attribute.impl.defense.*
import ac.github.oa.internal.attribute.impl.other.*
import ac.github.oa.internal.attribute.impl.update.*
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

    val decimalFormat: DecimalFormat
        get() = DecimalFormat(config.getString("options.decimal-format"))

    val simpleDateFormat: SimpleDateFormat
        get() = SimpleDateFormat(config.getString("options.time-format")!!)

    val json = Gson()

    val numberRegex = Regex("^[+-]?[1-9]*[0-9]?(\\.[0-9]{1,2})?\$")

    override fun onLoad() {

        Damage().register()
//        AddionDamage().register()
        BloodSucking().register()
        Crit().register()
        Hit().register()
//        UltimateHarm().register()
        Armor().register()
        Dodge().register()
        Rebound().register()
        ExpAddon().register()
        HealthRecovery().register()
        Health().register()
        MoveSpeed().register()
        Special().register()
        AttackSpeed().register()
//        AttackDistance().register()
        ArmorBreak().register()
//        RangeDamage().register()
//        Shield().register()
//        ShieldRecovery().register()
        JumpDamage().register()
        DirectionDamage().register()
        Metrics(12489, BukkitPlugin.getInstance().description.version, Platform.BUKKIT)
    }

    val original: Boolean
        get() = config.getBoolean("options.original")

    val skipStrings: List<String>
        get() = config.getStringList("options.skip-strings")


    override fun onEnable() {
        // Plugin startup logic

        // 属性 Enable 阶段
        for (attribute in AttributeManager.attributes) {
            AttributeManager.enable(attribute)
        }
        OriginPluginEnableEvent().call()
//        if (config.getBoolean("options.damage-effect-limit")) {
//            hookExecute("ProtocolLib") { unused: Void? -> DamageEffectLimit() }
//        }

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
        // Plugin shutdown logic
        // 属性 Disable 阶段
        for (attribute in AttributeManager.attributes) {
            attribute.disable()
        }
    }

}