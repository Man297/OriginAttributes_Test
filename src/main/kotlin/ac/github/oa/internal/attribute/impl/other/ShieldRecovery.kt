package ac.github.oa.internal.attribute.impl.other

import ac.github.oa.OriginAttribute
import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.attribute.AttributeData
import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.attribute.impl.defense.Shield
import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import kotlin.math.roundToLong

class ShieldRecovery : SingleAttributeAdapter(AttributeType.OTHER) {
    companion object {
        val map: HashMap<LivingEntity, Double> = HashMap()
    }

    override fun defaultOption(config: BaseConfig) {
        config.select(this)
                .setStrings("护盾恢复")
                .set("combat-power", 1)
                .set("period", 100)
                .superior()
    }

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {}

    override fun enable() {
        Bukkit.getPluginManager().getPlugin("OriginAttribute")?.let {
            object : BukkitRunnable(){
                override fun run() {
                    for (player in ArrayList<Player>(Bukkit.getOnlinePlayers())){
                        if (!player.isDead && player.isOnline){
                            val attributeData: AttributeData = OriginAttributeAPI.getAttributeData(player)
                            val maxShield: Double = attributeData.find(Shield::class.java)[0].number()
                            val shieldRecovery: Double = attributeData.find(ShieldRecovery::class.java)[0].number()
                            var shield: Double = 0.0
                            if (map.containsKey(player)){
                                shield = map[player]!!
                            }else{
                                map[player] = maxShield;
                                shield = map[player]!!
                            }
                            if (shield + shieldRecovery < maxShield){
                                map[player] = shield + shieldRecovery
                            }else{
                                map[player] = maxShield
                            }
                        }
                    }
                }
            }.runTaskTimerAsynchronously(it, 20, baseConfig.select(this).any("period").asNumber().toLong())
        }
    }

    override fun count(baseDoubles: Array<BaseDouble>): Long {
        return (baseDoubles[0].number() * baseConfig.select(this).any("combat-power").asNumber().toDouble()).roundToLong()
    }

    override fun format(entity: LivingEntity, s: String, baseDoubles: Array<BaseDouble>): Any {
        return if (s.equals("value")){
            if (map.containsKey(entity)){
                map[entity]!!
            }else{
                0
            }
        }else{
            baseDoubles[0].number()
        }
    }

    override val strings: Array<String>
        get() = arrayOf("护盾恢复")
    override val type: ValueType
        get() = ValueType.ALL
}
