package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.api.event.plugin.AttributeLoadEvent
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import ac.github.oa.internal.base.event.impl.UpdateMemory
import ac.github.oa.internal.core.attribute.*
import jdk.internal.dynalink.beans.StaticClass
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.io.newFile
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.reflect.Reflex.Companion.invokeConstructor
import taboolib.common.reflect.Reflex.Companion.invokeMethod
import taboolib.common.util.random
import taboolib.common5.compileJS
import taboolib.common5.scriptEngineFactory
import taboolib.library.configuration.ConfigurationSection
import java.io.File
import java.nio.charset.StandardCharsets
import javax.script.Compilable
import javax.script.Invocable
import javax.script.ScriptEngine

object Script {

    val cache = mutableMapOf<String, Int>()

    private val staticClasses = mutableMapOf<String, Any>()

    fun registerStaticClass(key: String, value: Any) {
        this.staticClasses[key] = value
        AttributeManager.usableAttributes.values.filterIsInstance<ScriptAttribute>().forEach {
            it.entry.scriptEngine.put(key, value)
        }
    }

    @Awake(LifeCycle.ENABLE)
    fun onLoad() {
        val config = AttributeManager.config
        config.getConfigurationSection("script")?.getKeys(false)?.forEach {

            val section = config.getConfigurationSection("script.${it}")!!
            if (it == "javascript-def0") {
                releaseResourceFile("attribute/javascript-def0.js", false)
            }

            val scriptAttribute = ScriptAttribute(section, "")
            if (scriptAttribute.types.size == 1 && AttributeType.OTHER in scriptAttribute.types) {
                // 为单 other 属性
                enableScriptAttribute(scriptAttribute)
            } else {
                val file = File(getDataFolder(), "attribute/${it}.js")
                if (file.exists()) {
                    scriptAttribute.script = file.readText(StandardCharsets.UTF_8)
                    enableScriptAttribute(scriptAttribute)
                } else {
                    info("|- The 'attribute/${it}.js' attribute script was not found.")
                }
            }
        }
    }

    private fun enableScriptAttribute(scriptAttribute: ScriptAttribute) {
        val priority = AttributeManager.getPriority(scriptAttribute)
        if (priority != -1) {
            scriptAttribute.setPriority(priority)
            AttributeManager.usableAttributes[priority] = scriptAttribute
            AttributeManager.enableAttribute(scriptAttribute)
        }
    }

    fun reloadScripts() {
        AttributeManager.usableAttributes.values.filterIsInstance<ScriptAttribute>().forEach {
            it.root = AttributeManager.config.getConfigurationSection("script.${it.toName()}")!!
            it.script = newFile(getDataFolder(), "attribute/${it.toName()}.js", create = true).readText()
            it.entry.onEnable()
        }
    }


    @Abstract
    class ScriptAttribute(override var root: ConfigurationSection, var script: String) : AbstractAttribute() {

        override val types: Array<AttributeType>
            get() = root.getEnumList("types", AttributeType::class.java).toTypedArray()

        override fun onLoad() {

            entries += entry
            entry.name = this.toName()
            entry.node = this
            entry.index = 0
            entry.onEnable()

            info("|- Registered attribute ${toName()}.")
        }

        override fun toName(): String {
            return root.name
        }

        override fun onReload() {
            cache.clear()
            AttributeManager.config.reload()
            reloadScripts()
        }

        override fun toValue(player: Player, attributeData: AttributeData, args: Array<String>): Any? {
            val arg = if (args.size == 3) args[2] else ""
            return entry.toValue(player, arg, attributeData.getData(this.index, entry.index))
        }

        val entry = Entry()

    }


    class Entry : Attribute.Entry() {

        override val type: Attribute.Type
            get() = getRoot().getEnum("value-type", Attribute.Type::class.java)!!

        val scriptEngine: ScriptEngine = scriptEngineFactory.scriptEngine

        val invocable: Invocable
            get() = scriptEngine as Invocable

        val scriptAttribute by lazy { node as ScriptAttribute }

        override fun onEnable() {
            if (scriptAttribute.script.isEmpty()) return
            scriptEngine.put("name", name)
            scriptEngine.put("index", index)
            scriptEngine.put("api", ScriptAPI)
            staticClasses.forEach { scriptEngine.put(it.key, it.value) }
            scriptEngine.eval(scriptAttribute.script)
        }

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            if (scriptAttribute.script.isEmpty()) return
            invocable.invokeFunction("handler", memory, data)
        }


        @Suppress("UNCHECKED_CAST")
        override fun getCorrectRules(): List<List<Double>> {
            return getRoot().getList("correct") as List<List<Double>>
        }

        override fun getKeywords(): List<String> {
            return getRoot().getStringList("keywords")
        }

    }

}


object ScriptAPI {

    fun info(any: Any) {
        taboolib.common.platform.function.info(any.toString())
    }

    fun tell(entity: LivingEntity, any: Any) {
        entity.sendMessage(any.toString())
    }

//    fun getClass(clazz: String) = StaticClass.forClass(Class.forName(clazz))
//
//    fun newInstance(clazz: String, vararg args: Any) = Class.forName(clazz).invokeConstructor(args)

    fun chance(value: Double): Boolean {
        return random(value)
    }

    fun getData(context: EventMemory, entity: LivingEntity, index: Int, entry: Int): AttributeData.Data {
        return getData(context, entity, index).get(entry)
    }

    fun getData(context: EventMemory, entity: LivingEntity, index: Int): Array<AttributeData.Data> {
        return getAttributeData(context, entity).getArrayData(index)
    }

    fun getData(context: EventMemory, entity: LivingEntity, keyword: String): AttributeData.Data {
        val index = Script.cache.computeIfAbsent(keyword) {
            AttributeManager.usableAttributes.values.firstOrNull {
                keyword in it.toEntities().flatMap { it.getKeywords() }
            }?.getPriority() ?: -1
        }
        if (index == -1) error("Attribute [$keyword] not found.")
        val entry = AttributeManager.getAttribute(index).searchByKeyword(keyword)
        val attributeData = getAttributeData(context, entity)
        val arrayData = attributeData.getArrayData(index)
        return arrayData[entry.index]
    }

    fun getAttributeData(context: EventMemory?, entity: LivingEntity): AttributeData {
        if (context == null) return OriginAttributeAPI.getAttributeData(entity)

        if (context is DamageMemory) {
            if (context.attacker == entity) {
                return context.attackAttributeData
            } else if (context.injured == entity) {
                return context.injuredAttributeData
            }
        } else if (context is UpdateMemory) {
            if (context.livingEntity == entity) {
                return context.attributeData
            }
        }
        return OriginAttributeAPI.getAttributeData(entity)
    }

}
