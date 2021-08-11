package ac.github.oa.internal.script

import java.util.*

/**
 * @author AmazingOcean on 2021/5/17
 */
object InternalScriptManager {

    var internalScripts: MutableList<InternalScript<*>> = arrayListOf()

    fun filter(key: String): InternalScript<*>? {
        return internalScripts.firstOrNull { it.name == key }
    }
}