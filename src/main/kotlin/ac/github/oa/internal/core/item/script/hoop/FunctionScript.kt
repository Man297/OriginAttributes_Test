package ac.github.oa.internal.core.item.script.hoop

import ac.github.oa.internal.core.item.script.InternalConfig
import ac.github.oa.internal.core.item.script.InternalScript
import ac.github.oa.internal.core.item.script.content.JavaScriptPlant
import ac.github.oa.util.Strings
import org.bukkit.entity.Entity
import javax.script.Invocable

object FunctionScript : InternalScript<MapScript.Wrapper> {

    override val name: String
        get() = "func"

    override fun execute(entity: Entity?, wrapper: MapScript.Wrapper, config: InternalConfig, string: String): String? {
        var aString = string
        val args = arrayListOf<String>()
        if (string.contains('(') && string.contains(')')) {
            val result = Strings.readVariables(string, '(', ')')[0]
            result.split(',').forEach {
                args.add(it)
            }
            aString = string.substring(0, string.indexOf('('))
        }
        val compiledScript = JavaScriptPlant.map[aString] ?: error("未找到脚本 $aString")
        val scriptEngine = compiledScript.engine
        compiledScript.eval(scriptEngine.context)
        val invocable = scriptEngine as Invocable
        val function = invocable.invokeFunction("main", Context(entity, config, string), args)
        return function.toString()
    }

    class Context(
        entity: Entity?,
        config: InternalConfig,
        string: String
    )

}