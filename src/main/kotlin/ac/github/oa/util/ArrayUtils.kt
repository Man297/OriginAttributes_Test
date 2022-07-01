package ac.github.oa.util

import ac.github.oa.internal.core.attribute.AttributeData
import ac.github.oa.internal.core.item.script.InternalScript
import ac.github.oa.internal.core.item.script.hoop.MapScript
import org.bukkit.entity.LivingEntity

object ArrayUtils {

    /**
     * damage +{eval:{map:value}*10+2.0}
     */
    fun read(entity: LivingEntity, data: AttributeData, strings: List<String>, amount: Double): List<String> {
        val listOf = mutableListOf(*strings.toTypedArray())
        val wrapper = MapScript.Wrapper()
        wrapper["value"] = amount
        wrapper["data"] = data
        listOf.forEachIndexed { index, s ->
            listOf[index] = InternalScript.transform(s, entity, listOf(wrapper))
        }
        return listOf
    }
}
