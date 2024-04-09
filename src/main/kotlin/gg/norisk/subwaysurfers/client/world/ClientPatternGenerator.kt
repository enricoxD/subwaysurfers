package gg.norisk.subwaysurfers.client.world

import gg.norisk.subwaysurfers.client.lifecycle.ClientGameRunningLifeCycle
import gg.norisk.subwaysurfers.common.structure.StructureManager
import gg.norisk.subwaysurfers.common.world.AbstractPatternGenerator
import gg.norisk.subwaysurfers.entity.RampEntity
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.BlockMirror
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.silkmc.silk.core.world.block.BlockInfo
import java.util.*

open class ClientPatternGenerator(
    startPos: BlockPos,
    patternStack: Stack<Stack<String>>,
    structureManager: StructureManager,
    ignoreAir: Boolean = true,
    mirror: BlockMirror = BlockMirror.NONE
) : AbstractPatternGenerator(startPos, patternStack, structureManager, ignoreAir, mirror) {

    override fun onBlockPlace(blockPos: BlockPos, blockState: BlockState?, world: World) {
        if (blocksToPlace[blockPos]?.isAir == false) {
            world.setBlockState(blockPos, blocksToPlace[blockPos])
        }
        ClientGameRunningLifeCycle.fakeBlocks.add(BlockInfo(Blocks.AIR.defaultState, blockPos))
    }

    override fun onEntitySpawn(entity: Entity, player: PlayerEntity, world: World) {
        (world as ClientWorld).addEntity(entity)
        entity.streamSelfAndPassengers().forEach(world::spawnEntity)

        if (entity is RampEntity) {
            entity.placeStairs()
        }
    }
}
