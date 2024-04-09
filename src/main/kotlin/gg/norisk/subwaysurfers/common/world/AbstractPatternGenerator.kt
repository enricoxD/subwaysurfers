package gg.norisk.subwaysurfers.common.world

import gg.norisk.subwaysurfers.common.structure.StructureManager
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.StructureTemplate
import net.minecraft.util.BlockMirror
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

abstract class AbstractPatternGenerator(
    val startPos: BlockPos,
    var patternStack: Stack<Stack<String>>,
    val structureManager: StructureManager,
    var ignoreAir: Boolean = true,
    var mirror: BlockMirror = BlockMirror.NONE,
) {

    companion object {
        val leftOffset = 4.0
        val rightOffset = -20.0
        val offset = 0.0
    }

    var nextZ = startPos.z
    var currentPatternStack: Stack<String> = patternStack.pop()
    var lastStructure: String = ""
    var currentStructure: StructureTemplate? = handleNextStructure()
    var blocksToPlace = mutableMapOf<BlockPos, BlockState>()
    val entitiesToPlace = mutableSetOf<Entity>()

    private fun handleNextStructure(): StructureTemplate? {
        if (currentPatternStack.isNotEmpty()) {
            lastStructure = currentPatternStack.pop()
            return structureManager.readOrLoadTemplate(lastStructure)
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

    open fun getGenerationPos(player: PlayerEntity, structureTemplate: StructureTemplate): Int {
        return player.blockPos.z + 48
    }

    open fun tick(player: PlayerEntity) {
        if (currentStructure == null) {
            //TODO maybe hier z resetten?
            currentStructure = handleNextStructure()
            return
        }

        handleBlockPlace(player)
        handleEntitySpawn(player)
        handleStructurePlacement(player)
    }

    open fun onPlace(player: PlayerEntity) {}

    private fun handleStructurePlacement(player: PlayerEntity) {
        val toPlace = currentStructure ?: return
        if (nextZ < getGenerationPos(player, toPlace)) {
            val xOffset = calculateXOffset(toPlace)

            structureManager.placeStructure(
                player,
                BlockPos(startPos.x + xOffset, startPos.y, nextZ),
                toPlace,
                StructurePlacementData().setMirror(mirror),
                ignoreAir,
                blocksToPlace,
                entitiesToPlace
            )

            onPlace(player)

            nextZ += toPlace.size.z

            currentStructure = handleNextStructure()
        }
    }

    private fun handleEntitySpawn(player: PlayerEntity) {
        val offset = 16
        val entitiesToRemove = mutableSetOf<Entity>()
        for (entity in entitiesToPlace) {
            if (entity.z - offset < player.z) {
                entitiesToRemove.add(entity)
                onEntitySpawn(entity, player, player.world)
            }
        }
        entitiesToPlace.removeAll(entitiesToRemove)
    }

    private fun handleBlockPlace(player: PlayerEntity) {
        val world = player.world
        val offset = 16
        val toRemove: MutableSet<BlockPos> = HashSet()
        for (blockPos in blocksToPlace.keys) {
            if (blockPos.z - offset < player.z) {
                toRemove.add(blockPos)
            }
        }

        for (blockPos in toRemove) {
            onBlockPlace(blockPos, blocksToPlace[blockPos], world)
            blocksToPlace.remove(blockPos)
        }
    }

    abstract fun onBlockPlace(blockPos: BlockPos, blockState: BlockState?, world: World)
    abstract fun onEntitySpawn(entity: Entity, player: PlayerEntity, world: World)
}
