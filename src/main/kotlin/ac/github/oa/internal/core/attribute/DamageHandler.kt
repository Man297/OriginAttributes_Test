package ac.github.oa.internal.core.attribute

import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.api.event.plugin.OriginPluginReloadEvent
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.event.impl.DamageMemory
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common5.compileJS
import taboolib.platform.util.sendActionBar
import javax.script.CompiledScript

object DamageHandler {

    var handlerScript: CompiledScript? = null

    @Awake(LifeCycle.ENABLE)
    fun compile() {
        handlerScript = AttributeManager.config.getString("damage-handler")?.compileJS()
    }

    @SubscribeEvent
    fun e(e: OriginPluginReloadEvent) {
        this.compile()
    }

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun e(e: EntityDamageEvent) {
        if (!e.isCancelled && !e.damageMemory.event.isCancelled && e.priorityEnum == PriorityEnum.POST) {
            if (handlerScript != null) {
                submit(async = true) {
                    val bindings = handlerScript!!.engine.createBindings()
                    bindings["openAPI"] = DamageEventOpenAPI(e)
                    handlerScript!!.eval(bindings)
                }
            }

        }
    }

    class DamageEventOpenAPI(val e: EntityDamageEvent) {

        // 取标签值
        fun getLabel(key: String): Any? {
            return e.damageMemory.labels[key]
        }

        // 目标标签值是否存在
        fun hasLabel(key: String): Boolean {
            return e.damageMemory.labels.containsKey(key)
        }

        // 打印
        fun println(vararg any: Any) {
            info(any)
        }

        // 攻击者
        val attacker: LivingEntity
            get() = e.damageMemory.attacker

        // 被攻击者
        val defender: LivingEntity
            get() = e.damageMemory.injured

        // 总伤害
        val totalDamage: Double
            get() = e.damageMemory.totalDamage

        // 伤害来源
        val damageSources: List<DamageMemory.Source>
            get() = e.damageMemory.getDamageSources()

        // 发送 action bar消息
        fun sendAction(player: Player, message: String) {
            player.sendActionBar(message)
        }

    }


}