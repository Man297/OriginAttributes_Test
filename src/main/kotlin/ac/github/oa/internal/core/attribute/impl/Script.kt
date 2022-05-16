package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.api.event.plugin.AttributeLoadEvent
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.core.attribute.*
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common5.compileJS
import taboolib.common5.scriptEngineFactory
import taboolib.library.configuration.ConfigurationSection
import java.io.File
import javax.script.Compilable
import javax.script.Invocable
import javax.script.ScriptEngine

object Script {

    @Awake(LifeCycle.ENABLE)
    fun onLoad() {
        val config = AttributeManager.config
        config.getConfigurationSection("script")?.getKeys(false)?.forEach {

            val section = config.getConfigurationSection("script.${it}")!!
            if (it == "javascript-def0") {
                releaseResourceFile("attribute/javascript-def0.js", false)
            }

            val file = File(getDataFolder(), "attribute/${it}.js")
            if (file.exists()) {
                val scriptAttribute = ScriptAttribute(section, file.readText())
                val priority = AttributeManager.getPriority(scriptAttribute)
                if (priority != -1) {
                    scriptAttribute.setPriority(priority)
                    AttributeManager.usableAttributes[priority] = scriptAttribute
                    AttributeManager.enableAttribute(scriptAttribute)
                }
            } else {
                info("|- The 'attribute/${it}.js' attribute script was not found.")
            }
        }
    }


    @Abstract
    class ScriptAttribute(override var root: ConfigurationSection, val script: String) : AbstractAttribute() {

        override val types: Array<AttributeType>
            get() = root.getEnumList("types", AttributeType::class.java).toTypedArray()

        override fun onLoad() {

            entries += entry
            entry.name = this.toName()
            entry.index = 0
            entry.onEnable()

            info("|- Registered attribute ${toName()}.")
        }

        override fun toName(): String {
            return root.name
        }

        override fun onReload() {
            AttributeManager.config.reload()
        }

        val entry = object : Attribute.Entry() {

            override val type: Attribute.Type
                get() = root.getEnum("value-type", Attribute.Type::class.java)!!

            lateinit var scriptEngine: ScriptEngine

            val invocable: Invocable
                get() = scriptEngine as Invocable

            override fun onEnable() {
                scriptEngine = scriptEngineFactory.scriptEngine
                scriptEngine.put("name", name)
                scriptEngine.put("index", index)
                scriptEngine.put("api", ScriptAPI)
                scriptEngine.eval(script)
            }

            override fun handler(memory: EventMemory, data: AttributeData.Data) {
                invocable.invokeFunction("handler", memory, data)
            }


            @Suppress("UNCHECKED_CAST")
            override fun getCorrectRules(): List<List<Double>> {
                return root.getList("correct") as List<List<Double>>
            }

            override fun getKeywords(): List<String> {
                return root.getStringList("keywords")
            }

        }

    }

}

object ScriptAPI {

    fun info(vararg any: Any) {
        info(any)
    }

}
