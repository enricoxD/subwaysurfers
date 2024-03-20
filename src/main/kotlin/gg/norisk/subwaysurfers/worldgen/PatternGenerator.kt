package gg.norisk.subwaysurfers.worldgen

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.StructureTemplate
import net.minecraft.util.BlockMirror
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.silkmc.silk.core.text.literal
import java.util.*

open class PatternGenerator(
    val startPos: BlockPos,
    var patternStack: Stack<Stack<String>>,
    var ignoreAir: Boolean = true,
    var mirror: BlockMirror = BlockMirror.NONE,
) {
    var nextZ = startPos.z
    var debug = false
    var currentPatternStack: Stack<String> = patternStack.pop()
    var lastStructure: String = ""
    var currentStructure: StructureTemplate? = handleNextStructure()

    //TODO erstmal aktuelles Pattern ablaufen lassen bevor wir neu handlen.
    private fun handleNextStructure(): StructureTemplate? {
        if (currentPatternStack.isNotEmpty()) {
            lastStructure = currentPatternStack.pop()
            return StructureManager.readOrLoadTemplate(lastStructure)
        } else {
            if (patternStack.isNotEmpty()) {
                currentPatternStack = patternStack.pop()
                return handleNextStructure()
            }
            return null
        }
    }

    open fun calculateXOffset(structureTemplate: StructureTemplate): Int {
        return if (mirror == BlockMirror.FRONT_BACK) {
            structureTemplate.size.x + 1
        } else {
            0
        }
    }

    open fun getGenerationPos(player: ClientPlayerEntity, structureTemplate: StructureTemplate): Int {
        return player.blockPos.z + (MinecraftClient.getInstance().options.viewDistance.value * 12).coerceAtMost(94)
    }

    fun tick(player: ClientPlayerEntity) {
        if (currentStructure == null) {
            //TODO maybe hier z resetten?
            currentStructure = handleNextStructure()
            return
        }

        if (nextZ < getGenerationPos(player, currentStructure!!)) {

            val xOffset = calculateXOffset(currentStructure!!)

            if (debug) {
                player.sendMessage("Placing $lastStructure at $nextZ size ${currentStructure?.size}".literal)
            }

            StructureManager.placeStructure(
                player,
                BlockPos(startPos.x + xOffset, startPos.y, nextZ),
                currentStructure!!,
                StructurePlacementData().setMirror(mirror),
                ignoreAir
            )

            if (currentStructure != null) {
                nextZ += (currentStructure?.size?.z ?: 0)
            }

            currentStructure = handleNextStructure()
        }
    }
}
