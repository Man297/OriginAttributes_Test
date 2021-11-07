import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import com.google.gson.GsonBuilder
import org.bukkit.Bukkit
import java.text.DateFormat
import java.util.concurrent.CompletableFuture

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
        }

        private fun run(): CompletableFuture<Void> {
            val completableFuture = CompletableFuture<Void>()
            (0..1000).forEach {
                println(it)
            }
            completableFuture.complete(null)
            return completableFuture
        }

    }

}