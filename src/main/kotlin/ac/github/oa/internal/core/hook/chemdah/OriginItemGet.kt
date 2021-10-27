package ac.github.oa.internal.core.hook.chemdah

import ac.github.oa.api.event.item.ItemCreatedEvent
import ink.ptms.chemdah.core.Data
import ink.ptms.chemdah.core.PlayerProfile
import ink.ptms.chemdah.core.quest.Task
import ink.ptms.chemdah.core.quest.objective.ObjectiveCountableI
import ink.ptms.chemdah.core.quest.objective.bukkit.IItemPick
import ink.ptms.chemdah.util.Function2
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import kotlin.reflect.KClass

object OriginItemGet : ObjectiveCountableI<ItemCreatedEvent>() {
    override val event: KClass<ItemCreatedEvent>
        get() = ItemCreatedEvent::class
    override val name: String
        get() = "origin get item"

    init {
        handler { entity as Player }

        addCondition0("key") { e ->
            toString() == e.config.name
        }

        addCondition0("position") { e ->
            toPosition().inside(e.entity!!.location)
        }
        addCondition0("item") { e ->
            toInferItem().isItem(e.itemStack)
        }
        addCondition0("amount") { e ->
            toInt() <= e.itemStack.amount
        }

        addCondition0("group") { e ->
            toString() == e.config.getString("group")
        }

        addConditionVariable("amount") {
            it.itemStack.amount
        }
    }

    /**
     * 添加条目继续的条件
     * 简化版本
     */
    fun addCondition0(name: String, func: Data.(ItemCreatedEvent) -> Boolean) {
        addCondition(name,object : Function2<Data,ItemCreatedEvent,Boolean> {
            override fun invoke(input1: Data, input2: ItemCreatedEvent): Boolean {
                return func(input1, input2)
            }
        })
    }

    override fun getCount(profile: PlayerProfile, task: Task, event: ItemCreatedEvent): Int {
        return event.itemStack.amount
    }

}