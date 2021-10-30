
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
            val list = listOf("123", "123", "123{\\n}456", "455").flatMap { it.split("{\\n}") }
            println(list)

        }
    }

}