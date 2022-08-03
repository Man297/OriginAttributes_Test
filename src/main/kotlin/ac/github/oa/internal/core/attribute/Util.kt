package ac.github.oa.internal.core.attribute

import taboolib.common5.Coerce
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.min

fun AttributeData.loadTo(attribute: Attribute, string: String) {
    attribute.toEntities().forEach {
        getData(attribute.getPriority(), it.index).loadTo(it, string)
    }
}

fun AttributeData.Data.loadTo(entry: Attribute.Entry, string: String) {
    val keywords = entry.getKeywords()
    if (keywords.any { it in string }) {
        if (entry.type == Attribute.Type.SINGLE) {
            add(0, Coerce.toDouble(getNumber(string)))
        }
        if (entry.type == Attribute.Type.RANGE) {
            val split = string.replace(FILTER_RULES[0],"").split(" - ")
            add(0, Coerce.toDouble(getNumber(split[0])))
            add(1, Coerce.toDouble(getNumber(if (split.size == 1) split[0] else split[1])))
        }
    }
}
val FILTER_RULES = listOf(
    Regex("§+[a-z0-9%]"),
    Regex("[^0-9+--.]"),
)

var pattern: Pattern = Pattern.compile(FILTER_RULES[1].pattern)

fun getNumber(string: String): String {
    var prey = string
    var m = pattern.matcher(prey)
    var list = m.replaceAll(" ").trim().split(" ").toList()
    prey = list.filter { it.isNotEmpty() }.stream().mapToDouble{ it.toDouble() }.sum().toString()
    return prey.ifEmpty { "0.0" }
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


fun Attribute.Entry.getRoot() = (node as AbstractAttribute).root


fun AttributeData.getAttribute(index: Int): Attribute {
    return AttributeManager.usableAttributes[index] ?: error("[AttributeManager] $index not found.")
}

fun Attribute.toEntities(): List<Attribute.Entry> {
    return (this as AbstractAttribute).entries
}

fun Attribute.includesType(attributeType: AttributeType): Boolean {
    val types = (this as AbstractAttribute).types
    return types.indexOf(attributeType) != -1
}
