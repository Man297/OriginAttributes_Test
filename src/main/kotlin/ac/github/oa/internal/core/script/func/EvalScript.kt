package ac.github.oa.internal.core.script.func

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.core.script.BaseWrapper
import ac.github.oa.internal.core.script.InternalConfig
import ac.github.oa.internal.core.script.InternalScript
import ac.github.oa.util.ReportUtil
import org.bukkit.entity.Entity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

@Awake(LifeCycle.ENABLE)
class EvalScript : InternalScript<BaseWrapper> {
    override val name: String
        get() = "eval"

    override fun execute(entity: Entity?, wrapper: BaseWrapper, config: InternalConfig, string: String): String? {
        val result = ReportUtil.getResult(string)
        if (result.toString().contains(".")) {
            return OriginAttribute.decimalFormat.format(result)
        }
        return result.toString()
    }

}