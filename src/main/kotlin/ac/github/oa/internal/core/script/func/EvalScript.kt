package ac.github.oa.internal.core.script.func

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.core.script.BaseWrapper
import ac.github.oa.internal.core.script.InternalConfig
import ac.github.oa.internal.core.script.InternalScript
import ac.github.oa.util.ReportUtil
import org.bukkit.entity.Entity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.BukkitPlugin
import java.util.logging.Level

@Awake
class EvalScript : InternalScript<BaseWrapper> {
    override val name: String
        get() = "eval"

    override fun execute(entity: Entity?, wrapper: BaseWrapper, config: InternalConfig, string: String): String? {
        try {
            val result = ReportUtil.getResult(string)

            if (result.toString().contains(".")) {
                return OriginAttribute.decimalFormat.format(result)
            }
            return result.toString()
        } catch (e: Exception) {
            BukkitPlugin.getInstance().logger.log(Level.WARNING, "错误的表达式 $string")
        }
        return "0"
    }

}