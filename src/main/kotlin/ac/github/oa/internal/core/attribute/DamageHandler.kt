package ac.github.oa.internal.core.attribute

import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.api.event.plugin.OriginPluginReloadEvent
import org.bukkit.entity.LivingEntity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.compileJS
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
        if (!e.isCancelled && !e.damageMemory.event.isCancelled) {
            if (handlerScript != null) {
                val bindings = handlerScript!!.engine.createBindings()
                bindings["openAPI"] = DamageEventOpenAPI(e)
                handlerScript!!.eval(bindings)
            }

        }
    }

    class DamageEventOpenAPI(val e: EntityDamageEvent) {

        fun getLabel(key: String): Any? {
            return e.damageMemory.labels[key]
        }

        fun hasLabel(key: String): Boolean {
            return e.damageMemory.labels.containsKey(key)
        }

        val attacker: LivingEntity
            get() = e.damageMemory.attacker

        val defender: LivingEntity
            get() = e.damageMemory.injured

        val totalDamage: Double
            get() = e.damageMemory.totalDamage

    }


}