package ac.github.oa.internal.core.equip

interface SlotCondition {

    fun screen(string: String, keyword: List<String>): Boolean

}