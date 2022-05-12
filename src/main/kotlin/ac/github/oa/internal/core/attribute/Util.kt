package ac.github.oa.internal.core.attribute

import taboolib.common5.Coerce
import kotlin.math.max
import kotlin.math.min

val FILTER_RULES = listOf(
    Regex("§+[a-z0-9%]"),
    Regex("-[^0-9]"),
    Regex("[^-0-9.?]"),
    Regex("[^.]")
)

fun AttributeData.loadTo(attribute: Attribute, string: String) {
    attribute.toEntities().forEach {
        getData(attribute.getPriority(), it.index).loadTo(it, string)
    }
}

fun AttributeData.Data.loadTo(entry: Attribute.Entry, string: String) {
    val keywords = entry.getKeywords()
    if (keywords.any { it in string }) {
        val split = getNumber(string).split("-")
        if (entry.type == Attribute.Type.SINGLE) {
            add(0, Coerce.toDouble(split[0]))
        }
        if (entry.type == Attribute.Type.RANGE) {
            add(0, Coerce.toDouble(split[0]))
            add(1, Coerce.toDouble(if (split.size == 1) split[0] else split[1]))
        }
    }
}

fun getNumber(string: String): String {
    var prey = string
    if (string.contains(string)) {
        FILTER_RULES.forEach { prey = prey.replace(it, "") }
    }
    prey = prey.ifEmpty { "0.0" }
    return string
}

fun AttributeData.Data.correct(entry: Attribute.Entry) {
    val correctRules = entry.getCorrectRules()
    if (correctRules.isEmpty()) {
        this.array.forEachIndexed { index, d ->
            if (correctRules.size > index) {
                set(index, min(max(d, correctRules[index][1]), correctRules[index][0]))
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun Attribute.Entry.getCorrectRules(): List<List<Double>> {
    val path = "${this.name}.correct"
    val root = getRoot()
    return root.getMapList(path) as List<List<Double>>
}

fun Attribute.Entry.getRoot() = (node as AbstractAttribute).root

fun Attribute.Entry.getKeywords(): List<String> {
    return getRoot().getStringList("${this.name}.keywords")
}

fun AttributeData.getAttribute(index: Int): Attribute {
    return AttributeManager.usableAttributes[index] ?: error("[AttributeManager] $index not found.")
}

fun Attribute.toEntities(): List<Attribute.Entry> {
    return (this as AbstractAttribute).entries
}
