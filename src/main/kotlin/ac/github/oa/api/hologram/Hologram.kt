package ac.github.oa.api.hologram

import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

interface Hologram<T> {

    fun create(player: Player, location: Location, content: List<String>)

    fun teleport(location: Location)

    fun update(content: List<String>)

    fun delete()

    fun create(player: Player, location: Location, line: String): T

    fun update(obj: T, line: String)

    fun teleport(obj: T, location: Location)

    fun delete(obj: T)
}