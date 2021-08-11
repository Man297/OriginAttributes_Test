package ac.github.oa.internal.script

import java.lang.Exception
import org.bukkit.entity.Entity
import java.util.*

class InternalConfig(
    var key: String,
    var value: String,
    var string: String,
    var list: List<InternalConfig> = ArrayList()
) {

    fun format(entity: Entity?, wrapper: BaseWrapper): String? {
        var s = value
        for (config in list) {
            val format = config.format(entity, wrapper)
            s = s.replace("{" + config.string + "}", format!!)
        }
        return InternalScriptManager.filter(key)?.execute(entity, wrapper, string) ?: s
    }

    override fun toString(): String {
        return "Config{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", string='" + string + '\'' +
                ", list=" + list +
                '}'
    }

    companion object {
        fun parse(string: String): List<InternalConfig> {
            var start = -1
            var end = -1
            var index = 0
            val chars = string.toCharArray()
            val list: MutableList<InternalConfig> = ArrayList()
            for (i in chars.indices) {
                val aChar = chars[i]
                if (aChar == '{') {
                    if (start == -1) {
                        start = i + 1
                    } else {
                        index++
                    }
                } else if (aChar == '}') {
                    if (index != 0) {
                        index--
                    } else if (start != -1) {
                        end = i
                    }
                }
                if (index == 0 && start != -1 && end != -1) {
                    try {
                        val substring = string.substring(start, end)
                        val key = substring.split(":").toTypedArray()[0]
                        val value = if (key.length == substring.length) "" else substring.substring(key.length + 1)
                        val config = InternalConfig(key, value, substring, parse(value))
                        list.add(config)
                        start = -1
                        end = -1
                    } catch (ignored: Exception) {
                    }
                }
            }
            return list
        }
    }
}