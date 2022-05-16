import ac.github.oa.internal.core.attribute.getNumber

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val s = "物理攻击: 0.3%"
            println(getNumber(s))
        }


    }

    val FILTER_RULES = listOf(
        Regex("§+[a-z0-9%]"),
        Regex("-[^0-9]"),
        Regex("[^-0-9.?]")
    )

    fun getNumber(string: String): String {
        var prey = string
        FILTER_RULES.forEach { prey = prey.replace(it, "") }
        return prey.ifEmpty { "0.0" }
    }


}
