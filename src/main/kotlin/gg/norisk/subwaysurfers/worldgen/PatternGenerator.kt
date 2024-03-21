package gg.norisk.subwaysurfers.worldgen

import gg.norisk.subwaysurfers.client.lifecycle.ClientGameRunningLifeCycle
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.StructureTemplate
import net.minecraft.util.BlockMirror
import net.minecraft.util.math.BlockPos
import net.silkmc.silk.core.world.block.BlockInfo
import java.util.*

open class PatternGenerator(
    val startPos: BlockPos,
    var patternStack: Stack<Stack<String>>,
    var ignoreAir: Boolean = true,
    var mirror: BlockMirror = BlockMirror.NONE,
) {
    var nextZ = startPos.z
    var currentPatternStack: Stack<String> = patternStack.pop()
    var lastStructure: String = ""
    var currentStructure: StructureTemplate? = handleNextStructure()
    var blocksToPlace = mutableMapOf<BlockPos, BlockState>()
    val entitiesToPlace = mutableSetOf<Entity>()

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

        val world = player.world as ClientWorld

        handleBlockPlace(player, world)
        handleEntitySpawn(player, world)
        handleStructurePlacement(player)
    }

    open fun onPlace(player: ClientPlayerEntity) {}

    private fun handleStructurePlacement(player: ClientPlayerEntity) {
        val toPlace = currentStructure ?: return
        if (nextZ < getGenerationPos(player, toPlace)) {
            val xOffset = calculateXOffset(toPlace)

            StructureManager.placeStructure(
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

    private fun handleBlockPlace(player: ClientPlayerEntity, world: ClientWorld) {
        val offset = 8 * 16
        val toRemove: MutableSet<BlockPos> = HashSet()
        for (blockPos in blocksToPlace.keys) {
            if (blockPos.z - offset < player.z) {
                toRemove.add(blockPos)
            }
        }

        for (blockPos in toRemove) {
            world.setBlockState(blockPos, blocksToPlace[blockPos])
            ClientGameRunningLifeCycle.fakeBlocks.add(BlockInfo(Blocks.AIR.defaultState, blockPos))
            blocksToPlace.remove(blockPos)
        }
    }

    private fun handleEntitySpawn(player: ClientPlayerEntity, world: ClientWorld) {
        val offset = 8 * 16
        val entitiesToRemove = mutableSetOf<Entity>()
        for (entity in entitiesToPlace) {
            if (entity.z - offset < player.z) {
                world.addEntity(entity)
                entity.streamSelfAndPassengers().forEach(world::spawnEntity)
                entitiesToRemove.add(entity)
            }
        }
        entitiesToPlace.removeAll(entitiesToRemove)
    }
}
