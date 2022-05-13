package ac.github.oa.internal.core.item.generator

import ac.github.oa.internal.core.item.ItemPlant
import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info

object ItemGeneratorLoader {

    @Awake(LifeCycle.ENABLE)
    fun loadGenerators() {
        runningClasses.forEach {
            if (ItemGenerator::class.java.isAssignableFrom(it)) {
                val any = it.getInstance()?.get() ?: return@forEach
                ItemPlant.generators += any as ItemGenerator
                info("Loaded generator ${it.simpleName}")
            }
        }
    }

}