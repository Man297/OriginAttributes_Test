package ac.github.oa.util

import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.core.script.InternalScript
import ac.github.oa.internal.core.script.hoop.MapScript
import org.bukkit.Material
import java.util.*

object ArrayUtils {
    fun indexOf(objects: Array<*>, v: Any): Int {
        return listOf(*objects).indexOf(v)
    }

    fun merge(d1: Array<BaseDouble>, d2: Array<BaseDouble>) {
        for (i in d2.indices) {
            if (i < d1.size) {
                d1[i].merge(d2[i])
            }
        }
    }

    /**
     * damage +{eval:{map:value}*10+2.0}
     */
    fun read(strings: List<String>?, amount: Int): List<String> {
        val arrayList = ArrayList(strings)
        for (i in arrayList.indices) {
            val wrapper = MapScript.Wrapper()
            wrapper["value"] = amount
            arrayList[i] = InternalScript.transform(arrayList[i], null, wrapper)
        }
        return arrayList
    }
}