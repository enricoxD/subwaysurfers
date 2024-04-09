package gg.norisk.subwaysurfers.common.structure

import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.structure.StructurePlacementData
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.World

interface ModifiedStructureTemplate {
    fun modifiedPlace(
        world: World,
        blockPos: BlockPos,
        blockPos2: BlockPos,
        structurePlacementData: StructurePlacementData,
        random: Random,
        i: Int,
        ignoreAir: Boolean,
        blocks: MutableMap<BlockPos, BlockState>?,
        entities: MutableSet<Entity>?,
    ): Boolean
}
