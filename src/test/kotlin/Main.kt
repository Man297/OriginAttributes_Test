
import com.google.gson.GsonBuilder
import java.text.DateFormat

class Main {

    companion object {


        val json = GsonBuilder()
            .enableComplexMapKeySerialization()
            .serializeNulls()
            .setDateFormat(DateFormat.LONG)
            .setPrettyPrinting()
            .setVersion(1.0)
            .create()

        @JvmStatic
        fun main(args: Array<String>) {
            val list = listOf("123", "123", "§f百分之三十几率出现轻灵羽翼{\\n}每次命中敌人将会获得速度II的效果{\\n}11", "455").flatMap { it.split("{\\n}") }
            println(list)

        }
    }

}