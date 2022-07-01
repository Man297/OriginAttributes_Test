package ac.github.oa.internal.core.item.script

import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake

/**
 * @author AmazingOcean on 2021/5/17
 */
object InternalScriptManager {

    var internalScripts: MutableList<InternalScript<*>> = arrayListOf()

    fun filter(key: String): InternalScript<BaseWrapper>? {
        return internalScripts.firstOrNull { it.name == key }
    }

    @Awake(LifeCycle.ENABLE)
    fun load() {
        runningClasses.forEach {
            if (InternalScript::class.java.isAssignableFrom(it)) {
                val internalScript = it.getInstance()?.get() as? InternalScript<*> ?: return@forEach
                internalScript.register()
            }
        }
    }

    init {
//        RandomScript().register()
    }

}