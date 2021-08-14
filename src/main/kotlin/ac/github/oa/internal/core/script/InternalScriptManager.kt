package ac.github.oa.internal.core.script

import ac.github.oa.internal.core.script.func.RandomScript
import ac.github.oa.internal.core.script.hoop.MapScript

/**
 * @author AmazingOcean on 2021/5/17
 */
object InternalScriptManager {

    var internalScripts: MutableList<InternalScript<*>> = arrayListOf()

    fun filter(key: String): InternalScript<BaseWrapper>? {
        return internalScripts.firstOrNull { it.name == key }
    }

    init {
//        RandomScript().register()
    }

}