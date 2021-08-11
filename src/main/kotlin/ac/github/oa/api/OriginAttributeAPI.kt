package ac.github.oa.api

import ac.github.oa.api.event.entity.EntityGetterDataEvent
import ac.github.oa.api.event.entity.EntityUpdateEvent
import ac.github.oa.api.event.render.AttributeRenderStringEvent
import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.attribute.AttributeData
import ac.github.oa.internal.attribute.AttributeManager
import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.event.EventMemory
import ac.github.oa.internal.event.impl.DamageMemory
import ac.github.oa.internal.event.impl.UpdateMemory
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import taboolib.common.platform.submit
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

object OriginAttributeAPI {

    var map: MutableMap<UUID, MutableMap<String, AttributeData>> = HashMap<UUID, MutableMap<String, AttributeData>>()

    fun getAttributeData(livingEntity: LivingEntity): AttributeData {
        val attributeData = AttributeData()
        val data = AttributeManager[livingEntity.uniqueId]
        attributeData.merge(data)
        attributeData.entityEquipment = data.entityEquipment
        getExtras(livingEntity.uniqueId).values.forEach(attributeData::merge)
        val event = EntityGetterDataEvent(livingEntity, attributeData)
        event.call()
        return attributeData
    }

    fun async(task: () -> Unit) {
        submit {
            task()
        }
    }

    fun loadItems(equipment: EntityEquipment?): List<ItemStack> {
        val itemStacks: MutableList<ItemStack> = ArrayList(Arrays.asList(*equipment!!.armorContents))
        itemStacks.add(equipment.itemInMainHand)
        itemStacks.add(equipment.itemInOffHand)
        return itemStacks
    }

    fun loadEntityEquipment(livingEntity: LivingEntity) {
        val equipment = livingEntity.equipment
        val attributeData = AttributeData()
        attributeData.entityEquipment = equipment
        val itemStacks = loadItems(equipment)
        val list: MutableList<String> = ArrayList()
        itemStacks.forEach(Consumer { itemStack: ItemStack? ->
            if (itemStack != null && itemStack.hasItemMeta() && itemStack.itemMeta!!.hasLore()) {
                val lore = itemStack.itemMeta!!.lore
                list.addAll(lore!!)
            }
        })
        val event = AttributeRenderStringEvent(livingEntity, list)
        event.call()
        if (!event.isCancelled) {
            for (s in event.list) {
                for (attribute in AttributeManager.attributes) {
                    val doubles: Array<BaseDouble> = attributeData.find(attribute)
                    attribute.inject(livingEntity, s, doubles)
                }
            }
        }
        AttributeManager.set(livingEntity.uniqueId, attributeData)
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
        return map.computeIfAbsent(
            uuid,
            Function<UUID, MutableMap<String, AttributeData>> { u: UUID? -> HashMap<String, AttributeData>() })
    }


}