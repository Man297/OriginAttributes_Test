package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.internal.core.attribute.Attribute
import ac.github.oa.internal.core.attribute.toEntities
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.info


fun Attribute.searchByKeyword(keyword: String): Attribute.Entry {
    return toEntities().firstOrNull { keyword in it.getKeywords() } ?: error("Attribute [$keyword] not found.")
}
