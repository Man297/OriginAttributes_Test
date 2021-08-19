
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
            val string = "攻击力 +10"

        }
    }

}