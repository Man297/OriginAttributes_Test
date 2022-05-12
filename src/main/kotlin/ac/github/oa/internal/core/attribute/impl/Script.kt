package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.core.attribute.*
import taboolib.common.LifeCycle
import taboolib.common.io.newFile
import taboolib.common.platform.Awake
import taboolib.common.platform.function.getDataFolder
import taboolib.common5.compileJS
import taboolib.library.configuration.ConfigurationSection
import java.io.File

object Script {

    @Awake(LifeCycle.ENABLE)
    fun onLoad() {
        val config = AttributeManager.config
        config.getConfigurationSection("script")?.getKeys(false)?.forEach {
            val section = config.getConfigurationSection("script.${it}")!!
            val file = File(getDataFolder(), "attribute/${it}.js")
            if (file.exists()) {
                ScriptAttribute(section, file.readText())
            } else {
                error("The 'attribute/${it}.js' property script was not found.")
            }
        }
    }


    class ScriptEntry(override var node: Attribute) : Attribute.Entry() {
        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            newFile()
        }

    }

    class ScriptAttribute(override var root: ConfigurationSection, val script: String) : AbstractAttribute() {

        override val types: Array<AttributeType>
            get() = root.getEnumList("types", AttributeType::class.java).toTypedArray()

        val compileJS = script.compileJS()

    }


}
