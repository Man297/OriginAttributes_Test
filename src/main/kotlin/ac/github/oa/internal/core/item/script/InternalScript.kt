package ac.github.oa.internal.core.item.script

import ac.github.oa.util.getInterfaceT
import org.bukkit.entity.Entity

interface InternalScript<out T : BaseWrapper> {

    fun execute(entity: Entity?, wrapper: @UnsafeVariance T, config: InternalConfig, string: String): String? = null

    val name: String

    val genericType: Class<*>
        get() = getInterfaceT(this, 0)!!

    fun register() {
        InternalScriptManager.internalScripts.add(this)
    }

    companion object {

        fun transform(
            source: String,
            entity: Entity?,
            wrapper: List<BaseWrapper>,
            call: (InternalConfig, String) -> String = { _, s -> s }
        ): String {
            var source = source
            val configs: List<InternalConfig> = InternalConfig.parse(source)
            for (config in configs) {
                val format = config.format(entity, wrapper)
                if (format != null) {
                    source = source.replaceFirst("{" + config.string + "}", call(config, format))
                }
            }
            return source
        }
    }
}
