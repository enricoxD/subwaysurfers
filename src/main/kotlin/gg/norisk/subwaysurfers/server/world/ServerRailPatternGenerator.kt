package gg.norisk.subwaysurfers.server.world

import gg.norisk.subwaysurfers.common.structure.StructureManager
import net.minecraft.structure.StructureTemplate
import net.minecraft.util.math.BlockPos
import java.util.*

class ServerRailPatternGenerator(
    startPos: BlockPos,
    patternStack: Stack<Stack<String>>,
    structureManager: StructureManager,
    ignoreAir: Boolean = false
) : ServerPatternGenerator(
    startPos, patternStack, structureManager, ignoreAir
) {
    override fun calculateXOffset(structureTemplate: StructureTemplate): Int {
        return -(structureTemplate.size.x / 2)
    }
}
