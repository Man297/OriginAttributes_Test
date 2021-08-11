package ac.github.oa.internal.script

import org.bukkit.entity.Entity

interface InternalScript<out T : BaseWrapper> {

    fun execute(entity: Entity?, wrapper: @UnsafeVariance T, string: String): String?

    val name: String

    fun register() {
        InternalScriptManager.internalScripts.add(this)
    }

    companion object {

        fun transform(source: String, entity: Entity?, wrapper: BaseWrapper): String {
            var source = source
            val configs: List<InternalConfig> = InternalConfig.parse(source)
            for (config in configs) {
                val format = config.format(entity, wrapper)
                if (format != null) {
                    source = source.replace("{" + config.string + "}", format)
                }
            }
            return source
        }
    }
}