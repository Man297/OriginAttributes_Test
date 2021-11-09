package ac.github.oa.internal.base

import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.util.Strings
import org.bukkit.configuration.ConfigurationSection
import taboolib.common.platform.function.info

class BaseConfig(var parent: ConfigurationSection?, var config: ConfigurationSection) {


    constructor(config: ConfigurationSection) : this(config.parent, config) {}

    fun select(attribute: AttributeAdapter): BaseConfig {
        return select(Strings.parseLowerString(attribute::class.java.simpleName))
    }

    fun select(string: String): BaseConfig {
        val section = if (config.isConfigurationSection(string)) config.getConfigurationSection(
            string
        ) else config.createSection(string)
        return BaseConfig(config, section!!)
    }

    operator fun contains(string: String): Boolean {
        return config.getStringList("strings").stream().anyMatch { s: String? -> string.contains(s!!) }
    }

    fun analysis(name: String, s: String, valueType: ValueType, force: Boolean = false): BaseDouble {
        val contains: Boolean = this.select(name).contains(s)
        return if (contains || force) {
            AttributeAdapter.getNumber(s, valueType)
        } else {
            BaseDouble()
        }
    }

    fun analysis(
        attributeAdapter: AttributeAdapter,
        s: String,
        valueType: ValueType,
        force: Boolean = false
    ): BaseDouble {
        val simpleName: String = attributeAdapter::class.java.simpleName
        return this.analysis(Strings.parseLowerString(simpleName), s, valueType, force)
    }

    fun superior(): BaseConfig {
        return BaseConfig(parent ?: throw NullPointerException("config root null."))
    }

    operator fun set(string: String, value: Any): BaseConfig {
        config[string] = value
        return this
    }

    fun any(name: String): ConfigBaseObject {
        return ConfigBaseObject(config, name)
    }

    fun setStrings(vararg values: Any): BaseConfig {
        return setList("strings", *values)
    }

    fun setCombatPower(value: Double): BaseConfig {
        return set("combat-power", value)
    }

    fun setList(string: String, vararg values: Any): BaseConfig {
        return set(string, values)
    }
}