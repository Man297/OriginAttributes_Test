package ac.github.oa.internal.attribute.impl.update

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.api.event.entity.EntityGetterDataEvent
import ac.github.oa.api.event.plugin.OriginPluginEnableEvent
import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.attribute.AttributeData
import ac.github.oa.internal.attribute.AttributeManager
import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.util.ArrayUtils
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Listener
import taboolib.common.platform.event.SubscribeEvent
import java.util.ArrayList
import kotlin.math.floor
import kotlin.math.roundToLong

class Special : AttributeAdapter(0, AttributeType.UPDATE) {

    companion object {
        var list: MutableList<SpecialAttribute> = ArrayList()

        @SubscribeEvent
        fun e(e: OriginPluginEnableEvent?) {
            for (specialAttribute in list) {
                AttributeManager.register(specialAttribute)
            }
        }

        @SubscribeEvent
        fun e(e: EntityGetterDataEvent) {
            val attributeData: AttributeData = e.attributeData
            AttributeManager.attributes
                .stream()
                .filter { attributeAdapter -> attributeAdapter is SpecialAttribute }
                .forEach { attributeAdapter ->
                    val specialAttribute = attributeAdapter as SpecialAttribute
                    val baseDoubles: Array<BaseDouble> = attributeData.find(specialAttribute.name)
                    val number: Double = baseDoubles[0].number()
                    val strings: List<String> = ArrayUtils.read(specialAttribute.entries, floor(number).toInt())
                    val data: AttributeData = OriginAttributeAPI.loadList(e.livingEntity, strings)
                    e.attributeData.merge(data)
                }
        }

    }

    override fun defaultOption(config: BaseConfig) {
        config.select("Power")
            .set("placeholder", "Power")
            .set("combat-power", 0.5)
            .setStrings("力量")
            .setList("entries", "攻击力 +{eval:{map:value}*0.1}")
    }


    override fun enable() {
        val config: ConfigurationSection = baseConfig.config
        for (key in config.getKeys(false)) {
            val baseConfig: BaseConfig = this.baseConfig.select(key)
            val placeholder: String = baseConfig.any("placeholder").asString()!!
            val entries: List<String> = baseConfig.any("entries").asStringList()
            val strings: List<String> = baseConfig.any("strings").asStringList()
            val combatPower = baseConfig.any("combat-power").asNumber().toInt()
            val specialAttribute: SpecialAttribute =
                object : SpecialAttribute(
                    key,
                    placeholder,
                    strings.toTypedArray(),
                    combatPower,
                    entries,
                    AttributeType.OTHER
                ) {}
            list.add(specialAttribute)
        }
    }


    override fun inject(entity: LivingEntity?, string: String, baseDoubles: Array<BaseDouble>) {}

    override fun format(entity: LivingEntity, s: String, baseDoubles: Array<BaseDouble>): Any {
        return AttributeManager.attributes
            .stream()
            .filter { attributeAdapter -> attributeAdapter is SpecialAttribute }.count()
    }

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {}

    abstract class SpecialAttribute(
        override val name: String,
        var placeholder: String,
        override var strings: Array<String>,
        val combatPower: Int,
        var entries: List<String>,
        vararg attributeTypes: AttributeType
    ) : SingleAttributeAdapter(*attributeTypes) {


        override fun inject(entity: LivingEntity?, string: String, baseDoubles: Array<BaseDouble>) {
            if (strings.any { s: String? ->
                    string.contains(
                        s!!
                    )
                }) {
                baseDoubles[0].merge(getNumber(string, type))
            }
        }

        override fun count(baseDoubles: Array<BaseDouble>): Long {
            return ((baseDoubles[0].number() * combatPower).roundToLong())
        }

        override fun format(entity: LivingEntity, s: String, baseDoubles: Array<BaseDouble>): Any {
            return baseDoubles[0].number()

        }

        override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {}

        override val type: ValueType
            get() = ValueType.NUMBER

        override val createFile: Boolean
            get() = false
    }
}