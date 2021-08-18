package ac.github.oa.internal.base.task

import ac.github.oa.OriginAttribute
import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.attribute.AttributeData
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.submit
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

object AttributePool {

    var map = ConcurrentHashMap<Player, MutableList<BaseTimeAttribute>>()

    operator fun get(player: Player): MutableList<BaseTimeAttribute> {
        return map.computeIfAbsent(player) { p: Player ->
            Collections.synchronizedList(ArrayList())
        }
    }

    fun update(player: Player) {
        val list: MutableList<String> = ArrayList()
        get(player).forEach(Consumer { dataAttribute: BaseTimeAttribute -> list.addAll(dataAttribute.list) })
        val attributeData: AttributeData = OriginAttributeAPI.loadList(list)
        OriginAttributeAPI.setExtra(
            player.getUniqueId(),
            OriginAttribute::class.java.simpleName,
            attributeData
        )
        OriginAttributeAPI.callUpdate(player)
    }

    fun put(player: Player, dataAttribute: BaseTimeAttribute) {
        get(player).add(dataAttribute)
        update(player)
    }

    @Awake(LifeCycle.ENABLE)
    fun init() {
        submit(delay = 20) {
            for ((key, value) in map) {
                val anIf =
                    value.removeIf { dataAttribute: BaseTimeAttribute -> System.currentTimeMillis() > dataAttribute.stamp }
                if (anIf) {
                    update(key)
                }
            }
        }
    }
}