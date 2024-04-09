package gg.norisk.subwaysurfers.server.world

import gg.norisk.subwaysurfers.common.entity.CollectibleEntity
import gg.norisk.subwaysurfers.common.structure.StructureManager
import gg.norisk.subwaysurfers.common.world.AbstractPatternGenerator
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.BlockMirror
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

open class ServerPatternGenerator(
    startPos: BlockPos,
    patternStack: Stack<Stack<String>>,
    structureManager: StructureManager,
    ignoreAir: Boolean = true,
    mirror: BlockMirror = BlockMirror.NONE
) : AbstractPatternGenerator(startPos, patternStack, structureManager, ignoreAir, mirror) {
    val collectibles = mutableListOf<CollectibleEntity>()

    override fun tick(player: PlayerEntity) {
        super.tick(player)
        collectibles.removeIf { entity -> player.blockPos.z - 5 > entity.blockPos.z }
    }

    override fun onBlockPlace(blockPos: BlockPos, blockState: BlockState?, world: World) {
    }

    override fun onEntitySpawn(entity: Entity, player: PlayerEntity, world: World) {
        if (entity is CollectibleEntity) {
            collectibles.add(entity)
        }
    }
}
