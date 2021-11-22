import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.render.IRenderRule
import ac.github.oa.internal.base.render.RenderRuleHandler
import com.google.gson.GsonBuilder
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.text.DateFormat
import java.util.concurrent.CompletableFuture

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val stripColor = ChatColor.stripColor("§f[§a3§f]攻击力 +20")!!
            val listOf = arrayListOf("§f[§a3§f]攻击力 +20", "xasfasf", "4d5as45d")
            listOf.removeIf { RenderRuleHandler.proof(ChatColor.stripColor(it)!!, listOf("regex \\[[0-9]+\\].*")) }
            println(listOf)
//            val proof = RenderRuleHandler.proof(ChatColor.stripColor("§f[§a3§f]攻击力 +20")!!, listOf("regex \\[[0-9]+\\]\\s.*"))
//            println(proof)
        }
    }

}