package ac.github.oa.internal.attribute.abst

import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import org.bukkit.entity.LivingEntity
import java.util.*

abstract class SingleAttributeAdapter(vararg attributeTypes: AttributeType) :
    AttributeAdapter(1) {

    override fun defaultOption(config: BaseConfig) {
        config.select(this).setStrings(*strings)
    }

    override fun format(entity: LivingEntity, s: String, baseDoubles: Array<BaseDouble>): Any {
        return baseDoubles[0].number()
    }

    override fun inject(entity: LivingEntity?, string: String, baseDoubles: Array<BaseDouble>) {
        baseDoubles[0].merge(baseConfig.analysis(this, string, type))
    }

    abstract val strings: Array<String>
    abstract val type: ValueType
}