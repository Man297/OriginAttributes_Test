package ac.github.oa.internal.core.script.func

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.core.script.InternalConfig
import ac.github.oa.internal.core.script.InternalScript
import ac.github.oa.internal.core.script.hoop.MapScript
import ac.github.oa.internal.core.item.random.RandomPlant
import ac.github.oa.util.random
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.util.random

@Awake(LifeCycle.ENABLE)
class RandomScript : InternalScript<MapScript.Wrapper> {

    val units = mutableMapOf<String, RandomUnit>()

    interface RandomUnit {
        fun execute(entity: LivingEntity?, wrapper: MapScript.Wrapper, string: String): String?
    }

    init {
        units["group"] = GroupUnit()
        units["double"] = DoubleUnit()
        units["int"] = IntUnit()
    }

    override val name: String
        get() = "random"

    override fun execute(entity: Entity?, wrapper: MapScript.Wrapper, config: InternalConfig, string: String): String? {


        val split = string.split(":")
        val target = split[0]
        val substring = string.substring(target.length + 1)
        return units[target]?.execute(entity as LivingEntity?, wrapper, substring)
    }

    class IntUnit : RandomUnit {
        override fun execute(entity: LivingEntity?, wrapper: MapScript.Wrapper, string: String): String? {
            val split = string.split("-")
            val min = split[0].toInt()
            val max = if (split.size == 2) split[1].toInt() else min
            return (min..max).random().toString()
        }
    }


    class DoubleUnit : RandomUnit {
        override fun execute(entity: LivingEntity?, wrapper: MapScript.Wrapper, string: String): String? {
            val split = string.split("-")
            val min = split[0].toDouble()
            val max = if (split.size == 2) split[1].toDouble() else min
//            return ThreadLocalRandom.current().nextDouble(min,max).toString()
            return OriginAttribute.decimalFormat.format(random(min, max))
        }
    }


    class GroupUnit : RandomUnit {
        override fun execute(entity: LivingEntity?, wrapper: MapScript.Wrapper, string: String): String? {


            return when (val any = RandomPlant.configs[string]) {
//            return when (val any = wrapper[string]) {
                is String -> {
                    return any.random(wrapper, entity)
                }
                is List<*> -> {
                    return firstOrList(any.map { it.toString() }, entity, wrapper)
                }
                else -> null
            }
        }

        fun firstOrList(list: List<String>, entity: LivingEntity?, wrapper: MapScript.Wrapper): String? {
            val mutableList = list.toMutableList()
            mutableList.forEachIndexed { index, s ->
                val indexOf = s.indexOf('#')
                if (indexOf != -1) {
                    val random = s.substring(0, indexOf).random(wrapper, entity)
                    if (random(1) < random.toDouble()) {
                        return s.substring(indexOf + 1)
                    } else {
                        mutableList[index] = s.substring(indexOf + 1)
                    }
                }
            }
            return mutableList.random()
        }

    }


}