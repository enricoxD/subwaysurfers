package gg.norisk.subwaysurfers.client.mechanics

import gg.norisk.subwaysurfers.event.events.PlayerEvents
import gg.norisk.subwaysurfers.network.c2s.blockCollisionPacketC2S
import gg.norisk.subwaysurfers.network.c2s.horizontalCollisionPacketC2S
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object ClientCollisionManager {
    private var lastBlockCollision: BlockPos = BlockPos(0, 0, 0)

    fun init() {
        PlayerEvents.horionztalCollisionEvent.listen { event ->
            val player = event.player
            if (event.player.world.isClient && player.isSubwaySurfers) {
                horizontalCollisionPacketC2S.send(player.blockPos.z)
            }
        }
    }

    fun handleBlockCollision(blockState: BlockState, world: World, blockPos: BlockPos, entity: Entity) {
        if (entity is PlayerEntity && entity.isSubwaySurfers && world.isClient) {
            if (lastBlockCollision != blockPos) {
                lastBlockCollision = blockPos
                blockCollisionPacketC2S.send(blockPos.z)
            }
        }
    }
}
