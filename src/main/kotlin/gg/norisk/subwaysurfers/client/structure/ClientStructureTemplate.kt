package gg.norisk.subwaysurfers.client.structure

import net.minecraft.block.BlockState
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.structure.StructurePlacementData
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random

interface ClientStructureTemplate {
    fun placeClient(
        clientWorld: ClientWorld,
        blockPos: BlockPos,
        blockPos2: BlockPos,
        structurePlacementData: StructurePlacementData,
        random: Random,
        i: Int,
        ignoreAir: Boolean,
        blocks: MutableMap<BlockPos, BlockState>?,
        entities: MutableSet<Entity>?,
    ): Boolean

    fun tick(playerEntity: PlayerEntity)
}
