package gg.norisk.subwaysurfers.client.mechanics

import gg.norisk.subwaysurfers.event.events.PlayerEvents
import gg.norisk.subwaysurfers.network.c2s.blockCollisionPacketC2S
import gg.norisk.subwaysurfers.network.c2s.horizontalCollisionPacketC2S
import gg.norisk.subwaysurfers.network.dto.toDto
import gg.norisk.subwaysurfers.registry.BlockRegistry
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers

object ClientCollisionManager {
    val collidableBlocks = listOf(BlockRegistry.TOP_BARRIER, BlockRegistry.BOTTOM_BARRIER)
    fun init() {
        PlayerEvents.horionztalCollisionEvent.listen { event ->
            val player = event.player
            if (event.player.world.isClient && player.isSubwaySurfers) {
                horizontalCollisionPacketC2S.send(player.blockPos.toDto())
            }
        }
        PlayerEvents.blockCollisionEvent.listen { event ->
            if (!collidableBlocks.contains(event.blockState.block)) return@listen

            val player = event.player
            if (event.player.world.isClient && player.isSubwaySurfers) {
                //TODO Maybe Serverside in Future if too much cheater
                blockCollisionPacketC2S.send(player.blockPos.toDto())
            }
        }
    }
}