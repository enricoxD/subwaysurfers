package gg.norisk.subwaysurfers.event.events

import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.World
import net.silkmc.silk.core.event.Event

object PlayerEvents {
    open class PlayerEvent(val player: PlayerEntity)

    class PlayerBlockCollisionEvent(player: PlayerEntity, val blockState: BlockState) : PlayerEvent(player)
    class PlayerHorionztalCollisionEvent(player: PlayerEntity) : PlayerEvent(player)

    val blockCollisionEvent = Event.onlySync<PlayerBlockCollisionEvent>()
    val horionztalCollisionEvent = Event.onlySync<PlayerHorionztalCollisionEvent>()
}
