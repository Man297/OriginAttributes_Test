package ac.github.oa.internal.core.script

import java.lang.Exception
import org.bukkit.entity.Entity
import java.util.*

open class InternalConfig(
    var id: String?,
    var key: String,
    var value: String,
    var string: String,
    var list: List<InternalConfig> = ArrayList()
) {

    fun format(entity: Entity?, wrappers: List<BaseWrapper>): String? {
        var s = value
        for (config in list) {
            val format = config.format(entity, wrappers)
            s = s.replaceFirst("{" + config.string + "}", format!!)
        }
        val internalScript = InternalScriptManager.filter(key)
        if (internalScript != null) {
            val genericType = internalScript.genericType
            wrappers.forEach {
                if (genericType == BaseWrapper::class.java || genericType == it::class.java) {
                    s = internalScript.execute(entity, it, this, s) ?: s
                }
            }
        }
        return s
    }

    override fun toString(): String {
        return "InternalConfig(id=$id, key='$key', value='$value', string='$string', list=$list)"
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
                        var substring = string.substring(start, end)
                        val string = substring

                        val id = if (substring.startsWith("$")) {
                            val s = substring.substring(1, substring.indexOf(":"))
                            substring = substring.substring(s.length + 2);
                            s
                        } else null

                        val key = substring.split(":").toTypedArray()[0]
                        val value = if (key.length == substring.length) "" else substring.substring(key.length + 1)

                        val config = InternalConfig(id, key, value, string, parse(value))
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