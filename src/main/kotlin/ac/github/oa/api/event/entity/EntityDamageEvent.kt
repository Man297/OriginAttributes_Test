package ac.github.oa.api.event.entity

import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.event.impl.DamageMemory
import taboolib.common.platform.ProxyEvent

/**
 * type 0 = pre 1 = post
 */
class EntityDamageEvent(
    val damageMemory: DamageMemory,
    var priorityEnum: PriorityEnum
) : ProxyEvent() {

    override val allowCancelled: Boolean
        get() = true

}