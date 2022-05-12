package ac.github.oa.util

import ac.github.oa.internal.core.script.InternalScript
import ac.github.oa.internal.core.script.hoop.MapScript
import java.util.*

object ArrayUtils {

    /**
     * damage +{eval:{map:value}*10+2.0}
     */
    fun read(strings: List<String>?, amount: Int): List<String> {
        val arrayList = ArrayList(strings)
        for (i in arrayList.indices) {
            val wrapper = MapScript.Wrapper()
            wrapper["value"] = amount
            arrayList[i] = InternalScript.transform(arrayList[i], null, listOf(wrapper))
        }
        return arrayList
    }
}
