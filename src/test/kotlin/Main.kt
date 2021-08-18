import ac.github.oa.internal.core.script.InternalScript
import ac.github.oa.internal.core.script.hoop.MapScript
import ac.github.oa.internal.core.script.wrapper.VoidWrapper
import ac.github.oa.util.random
import ac.github.oa.util.rebuild
import com.google.gson.GsonBuilder
import com.mojang.brigadier.context.CommandContext
import org.bukkit.entity.LivingEntity
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
            val map = json.fromJson<Map<String, String>>("{\"品质\"：\"高级\"}", Map::class.java)
            println(map["品质"])

        }
    }

}