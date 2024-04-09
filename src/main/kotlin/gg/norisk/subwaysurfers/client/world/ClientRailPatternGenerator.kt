package gg.norisk.subwaysurfers.client.world

import gg.norisk.subwaysurfers.common.structure.StructureManager
import gg.norisk.subwaysurfers.subwaysurfers.debugMode
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.structure.StructureTemplate
import net.minecraft.util.Colors
import net.minecraft.util.math.BlockPos
import net.silkmc.silk.core.text.literalText
import java.util.*

class ClientRailPatternGenerator(
    startPos: BlockPos,
    patternStack: Stack<Stack<String>>,
    structureManager: StructureManager,
    ignoreAir: Boolean = false
) : ClientPatternGenerator(
    startPos, patternStack, structureManager, ignoreAir
) {
    override fun calculateXOffset(structureTemplate: StructureTemplate): Int {
        return -(structureTemplate.size.x / 2)
    }

    override fun onPlace(player: PlayerEntity) {
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
