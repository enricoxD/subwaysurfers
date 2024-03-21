package gg.norisk.subwaysurfers.worldgen

import gg.norisk.subwaysurfers.subwaysurfers.debugMode
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.structure.StructureTemplate
import net.minecraft.util.Colors
import net.minecraft.util.math.BlockPos
import net.silkmc.silk.core.text.literalText
import java.util.*

class RailPatternGenerator(
    startPos: BlockPos, patternStack: Stack<Stack<String>>, ignoreAir: Boolean = false
) : PatternGenerator(
    startPos, patternStack, ignoreAir
) {
    override fun calculateXOffset(structureTemplate: StructureTemplate): Int {
        return -(structureTemplate.size.x / 2)
    }

    override fun getGenerationPos(player: ClientPlayerEntity, structureTemplate: StructureTemplate): Int {
        return player.blockPos.z + 48
    }

    override fun onPlace(player: ClientPlayerEntity) {
        if (player.debugMode) {
            player.sendMessage(literalText {
                text("Placing ")
                text(lastStructure) {
                    color = Colors.YELLOW
                }
                text(" at ")
                text(nextZ.toString()) {
                    color = Colors.YELLOW
                }
            })
        }
    }
}
