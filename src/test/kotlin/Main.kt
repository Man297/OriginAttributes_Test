
class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val s = "§f§8[ICON:0008] §f§7破甲: 7.28% §7[0]"
            println(getNumber(s))
        }

        val FILTER_RULES = listOf(
            Regex("\\[[0-9+]]"),
            Regex("\\[ICON:[0-9]+]"),
            Regex("§+[a-z0-9%]"),
            Regex("-[^0-9]"),
            Regex("[^-0-9.?]"),
        )

        fun getNumber(string: String): String {
            var prey = string
            FILTER_RULES.forEach {
                prey = prey.replace(it, "")
            }
            return prey.ifEmpty { "0.0" }
        }

    }



}
