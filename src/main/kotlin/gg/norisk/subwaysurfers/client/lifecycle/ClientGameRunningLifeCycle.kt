package gg.norisk.subwaysurfers.client.lifecycle

import gg.norisk.subwaysurfers.entity.UUIDMarker
import gg.norisk.subwaysurfers.extensions.toStack
import gg.norisk.subwaysurfers.mixin.world.WorldAccessor
import gg.norisk.subwaysurfers.network.s2c.patternPacketS2C
import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurfer
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.world.block.BlockInfo

object ClientGameRunningLifeCycle {
    var fakeBlocks = mutableListOf<BlockInfo>()

    fun init() {
        patternPacketS2C.receiveOnClient { packet, context ->
            val player = MinecraftClient.getInstance().player ?: return@receiveOnClient
            val subwaySurfer = player as? SubwaySurfer ?: return@receiveOnClient
            player.leftWallPatternGenerator?.patternStack?.add(packet.left.toStack())
            player.railPatternGenerator?.patternStack?.add(packet.middle.toStack())
            player.rightWallPatternGenerator?.patternStack?.add(packet.right.toStack())
        }
    }

    fun clearFakeBlocksAndEntities(forceClear: Boolean = false) {
        mcCoroutineTask(sync = true, client = true) {
            val player = MinecraftClient.getInstance().player ?: return@mcCoroutineTask

            val toRemove = mutableListOf<BlockInfo>()
            for (fakeBlock in fakeBlocks) {
                val shouldClear = forceClear || fakeBlock.pos.z < player.z - 5
                if (shouldClear) {
                    player.world.setBlockState(fakeBlock.pos, Blocks.AIR.defaultState, Block.NOTIFY_ALL_AND_REDRAW)
                    toRemove.add(fakeBlock)
                }
            }
            if (forceClear) {
                val entityRemoval = mutableListOf<Entity>()
                for (entity in (player.world as WorldAccessor).invokeGetEntityLookup().iterate()) {
                    if (entity is UUIDMarker && entity.owner == player.uuid) {
                        entityRemoval.add(entity)
                    }
                }
                entityRemoval.forEach(Entity::discard)
            }
            fakeBlocks.removeAll(toRemove.toSet())
        }
    }
}
