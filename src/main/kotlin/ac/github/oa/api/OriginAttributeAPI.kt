package ac.github.oa.api

import ac.github.oa.OriginAttribute
import ac.github.oa.api.event.entity.EntityGetterDataEvent
import ac.github.oa.api.event.entity.EntityUpdateEvent
import ac.github.oa.api.event.entity.EntityLoadEquipmentEvent
import ac.github.oa.api.event.render.AttributeRenderStringEvent
import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.attribute.AttributeData
import ac.github.oa.internal.attribute.AttributeManager
import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import ac.github.oa.internal.base.event.impl.UpdateMemory
import ac.github.oa.internal.core.condition.ConditionManager
import ac.github.oa.internal.core.equip.*
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.platform.util.isNotAir
import taboolib.type.BukkitEquipment
import java.util.*
import java.util.function.Consumer

object OriginAttributeAPI {

    var map: MutableMap<UUID, MutableMap<String, AttributeData>> = HashMap<UUID, MutableMap<String, AttributeData>>()

    /**
     *
     */
    fun getAttributeData(livingEntity: LivingEntity): AttributeData {
        val attributeData = AttributeData()
        val data = AttributeManager[livingEntity.uniqueId]
        attributeData.merge(data)
        attributeData.entityEquipment = data.entityEquipment
        getExtras(livingEntity.uniqueId).values.forEach(attributeData::merge)
        val event = EntityGetterDataEvent(livingEntity, attributeData)
        event.call()
        attributeData.autoCombatPower()
        return attributeData
    }

    fun async(task: () -> Unit) {
        submit {
            task()
        }
    }

    fun remove(uuid: UUID) {
        AttributeManager.map.remove(uuid)
        map.remove(uuid)
    }

    fun loadItems(livingEntity: LivingEntity): List<AdaptItem> {
        val listOf = arrayListOf<AdaptItem>()

        listOf.add(AdaptItem(Hand(BukkitEquipment.HAND.getItem(livingEntity))))
        listOf.add(AdaptItem(OffHand(BukkitEquipment.OFF_HAND.getItem(livingEntity))))
        listOf.add(AdaptItem(Helmet(BukkitEquipment.HEAD.getItem(livingEntity))))
        listOf.add(AdaptItem(BreastPlate(BukkitEquipment.CHEST.getItem(livingEntity))))
        listOf.add(AdaptItem(Gaiter(BukkitEquipment.LEGS.getItem(livingEntity))))
        listOf.add(AdaptItem(Boot(BukkitEquipment.FEET.getItem(livingEntity))))

        // baubles load.
        if (livingEntity is Player) {
            val inventory = livingEntity.inventory
            OriginAttribute.config.getStringList("options.condition.slot.pattern.InventorySlot")?.forEach {
                val split = it.split(" ")
                val slot = split[0].toInt()
                val itemStack = inventory.getItem(slot)
                listOf.add(AdaptItem(InventorySlot(slot, itemStack)))
            }
        }

        return listOf
    }

    fun loadInventory(livingEntity: LivingEntity): List<AdaptItem> {
        val items = this.loadItems(livingEntity)
        val event = EntityLoadEquipmentEvent(livingEntity, items)
        event.call()
        return items
    }

    fun loadEntityEquipment(livingEntity: LivingEntity) {
        val equipment = livingEntity.equipment
        val attributeData = AttributeData()
        attributeData.entityEquipment = equipment

        val items = loadInventory(livingEntity)

        val list: MutableList<String> = ArrayList()

        items.forEach {
            val itemStack = it.item
            if (itemStack.isNotAir()) {
                // conditioning...

                if (ConditionManager.pre(livingEntity, it) && ConditionManager.screen(livingEntity, it)) {
                    it.enable = true
                    list.addAll(itemStack.itemMeta?.lore ?: listOf())
                }
            }
        }

        val event = AttributeRenderStringEvent(livingEntity, list)
        event.call()
        if (!event.isCancelled()) {
            for (s in event.list) {
                for (attribute in AttributeManager.attributes) {
                    val doubles: Array<BaseDouble> = attributeData.find(attribute)
                    attribute.inject(livingEntity, s, doubles)
                }
            }
        }

        AttributeManager[livingEntity.uniqueId] = attributeData

    }

    fun callDamage(damageMemory: DamageMemory) {
        // 优先触发攻击者
        call(AttributeType.ATTACK, damageMemory)
        call(AttributeType.DEFENSE, damageMemory)
    }

    fun callUpdate(entity: LivingEntity) {
        val entityUpdateEvent = EntityUpdateEvent(entity, getAttributeData(entity))
        entityUpdateEvent.call()
        entityUpdateEvent.priorityEnum = PriorityEnum.POST
        entityUpdateEvent.attributeData = getAttributeData(entity)
        entityUpdateEvent.call()
        call(AttributeType.UPDATE, UpdateMemory(entity, entityUpdateEvent.attributeData))
    }

    fun call(attributeType: AttributeType, value: Any) {
        var attributeData: AttributeData? = null
        if (value is DamageMemory) {
            val damageMemory: DamageMemory = value
            attributeData =
                if (attributeType === AttributeType.ATTACK) damageMemory.attackAttributeData else damageMemory.injuredAttributeData
        } else if (value is UpdateMemory) {
            val updateMemory: UpdateMemory = value
            attributeData = updateMemory.attributeData
        }
        attributeData?.filter(attributeType)?.forEach { pair ->
            val key: AttributeAdapter = pair.key
            val baseDoubles: Array<BaseDouble> = pair.value
            key.method(value as EventMemory, baseDoubles)
        }
    }


    fun loadList(list: List<String>): AttributeData {
        return loadList(null, list)
    }

    fun loadList(entity: LivingEntity?, list: List<String>): AttributeData {
        val attributeData = AttributeData()
        for (s in list) {
            for (attribute in AttributeManager.attributes) {
                val doubles: Array<BaseDouble> = attributeData.find(attribute)
                attribute.inject(entity, s, doubles)
            }
        }
        return attributeData
    }

    fun removeExtra(uuid: UUID, c: String) {
        getExtras(uuid).remove(c)
    }

    fun setExtra(uuid: UUID, c: String, attributeData: AttributeData) {
        getExtras(uuid)[c] = attributeData
    }

    fun getExtras(uuid: UUID): MutableMap<String, AttributeData> {
        return map.computeIfAbsent(uuid) { HashMap<String, AttributeData>() }
    }


}