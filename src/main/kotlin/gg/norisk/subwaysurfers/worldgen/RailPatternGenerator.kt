package gg.norisk.subwaysurfers.worldgen

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.structure.StructureTemplate
import net.minecraft.util.math.BlockPos
import java.util.*

class RailPatternGenerator(
    startPos: BlockPos,
    patternStack: Stack<Stack<String>>,
    ignoreAir: Boolean = false
) : PatternGenerator(
    startPos,
    patternStack,
    ignoreAir
) {
    override fun calculateXOffset(structureTemplate: StructureTemplate): Int {
        return -(structureTemplate.size.x / 2)
    }

    override fun getGenerationPos(player: ClientPlayerEntity, structureTemplate: StructureTemplate): Int {
        return player.blockPos.z + 48
    }
}
