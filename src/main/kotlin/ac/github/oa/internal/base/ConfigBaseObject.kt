package ac.github.oa.internal.base

import org.bukkit.configuration.ConfigurationSection
import java.util.*

class ConfigBaseObject(var config: ConfigurationSection, var name: String) {

    fun asString(def: String? = null): String? {
        return if (config.isString(name)) config.getString(name) else def
    }

    fun asNumber(): Number {
        return config[name] as Number
    }

    fun asBoolean(): Boolean {
        return config.getBoolean(name)
    }

    @Throws(ClassNotFoundException::class)
    fun `as`(s: String?): Any? {
        return null
    }

    fun asStringList(): List<String> {
        if (config.isList(name)) {
            return config.getStringList(name)
        } else {
            val o = config[name]
            if (o != null) {
                if (o.javaClass.isArray) {
                    val objects = o as Array<*>
                    return objects.map { it.toString() }
                }
            }
        }
        return ArrayList()
    }

    val value: Any?
        get() = config[name]
}