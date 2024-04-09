package gg.norisk.subwaysurfers.event.events

import net.minecraft.world.World
import net.silkmc.silk.core.event.Event

object WorldEvents {
    open class WorldEvent(val world: World)

    val clientJoinWorldEvent = Event.onlySync<WorldEvent>()
}
