package ac.github.oa.internal.core.script

import org.bukkit.entity.Entity
import taboolib.platform.BukkitPlugin

interface InternalScript<out T : BaseWrapper> {

    fun execute(entity: Entity?, wrapper: @UnsafeVariance T, config: InternalConfig, string: String): String? = null

    val name: String

    fun register() {
//        BukkitPlugin.getInstance().logger.info("注册InternalScript " + this.name)
        InternalScriptManager.internalScripts.add(this)
    }

    companion object {

        fun transform(
            source: String,
            entity: Entity?,
            wrapper: BaseWrapper,
            call: (InternalConfig, String) -> Unit = { _, _ -> }
        ): String {
            var source = source
            val configs: List<InternalConfig> = InternalConfig.parse(source)
            for (config in configs) {
                val format = config.format(entity, wrapper)
                if (format != null) {
                    source = source.replace("{" + config.string + "}", format)
                    call(config, format)
                }
            }
            return source
        }
    }
}