package ac.github.oa

import ac.github.oa.api.event.plugin.OriginPluginEnableEvent
import ac.github.oa.internal.attribute.AttributeManager
import ac.github.oa.internal.attribute.impl.attack.*
import ac.github.oa.internal.attribute.impl.defense.*
import ac.github.oa.internal.attribute.impl.update.*
import ac.github.oa.internal.attribute.impl.other.*
import ac.github.oa.internal.hook.OriginPlaceholder
import org.bukkit.Bukkit
import taboolib.common.platform.Plugin
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile
import java.text.DecimalFormat
import java.util.function.Consumer

object OriginAttribute : Plugin() {

    @Config("config.yml", migrate = true)
    lateinit var config: SecuredFile

    val decimalFormat: DecimalFormat
        get() = DecimalFormat(config.getString("options.decimal-format"))

    override fun onLoad() {
        Damage().register()
        AddionDamage().register()
        BloodSucking().register()
        Crit().register()
        Hit().register()
        UltimateHarm().register()
        Armor().register()
        Dodge().register()
        Rebound().register()
        ExpAddion().register()
        HealthRecovery().register()
        Health().register()
        MoveSpeed().register()
        Special().register()
        AttackSpeed().register()
        ArmorBreak().register()
    }

    override fun onEnable() {
        // Plugin startup logic

        // 属性 Enable 阶段
        for (attribute in AttributeManager.attributes) {
            AttributeManager.enable(attribute)
        }
        OriginPluginEnableEvent().call()
        if (config.getBoolean("options.damage-effect-limit")) {
//            hookExecute("ProtocolLib") { unused: Void? -> DamageEffectLimit() }
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