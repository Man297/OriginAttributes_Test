package ac.github.oa.util

import ac.github.oa.internal.core.script.InternalScript
import ac.github.oa.internal.core.script.hoop.MapScript
import org.bukkit.entity.LivingEntity
import java.util.*

object ArrayUtils {

    /**
     * damage +{eval:{map:value}*10+2.0}
     */
    fun read(entity: LivingEntity, strings: List<String>, amount: Double): List<String> {
        val listOf = mutableListOf(*strings.toTypedArray())
        val wrapper = MapScript.Wrapper()
        wrapper["value"] = amount
        listOf.forEachIndexed { index, s ->
            listOf[index] = InternalScript.transform(s, entity, listOf(wrapper))
        }
        return listOf
    }
}
