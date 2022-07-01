package ac.github.oa.internal.core.attribute.equip

interface SlotCondition {

    fun screen(string: String, keyword: List<String>): Boolean

}