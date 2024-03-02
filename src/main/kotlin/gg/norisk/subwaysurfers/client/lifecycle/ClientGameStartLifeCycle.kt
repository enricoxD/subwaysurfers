package gg.norisk.subwaysurfers.client.lifecycle

import gg.norisk.subwaysurfers.client.ClientSettings
import gg.norisk.subwaysurfers.entity.UUIDMarker
import gg.norisk.subwaysurfers.extensions.toBlockPos
import gg.norisk.subwaysurfers.extensions.toStack
import gg.norisk.subwaysurfers.mixin.world.WorldAccessor
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfersOrSpectator
import gg.norisk.subwaysurfers.worldgen.PatternGenerator
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.util.BlockMirror
import net.silkmc.silk.core.event.Event
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.world.block.BlockInfo
import java.util.*


object ClientGameStartLifeCycle : ClientTickEvents.EndWorldTick {
    var fakeBlocks = mutableListOf<BlockInfo>()

    var leftWallPatternGenerator: PatternGenerator? = null
    var railPatternGenerator: PatternGenerator? = null
    var rightWallPatternGenerator: PatternGenerator? = null

    val clientGameStartEvent = Event.onlySync<Unit>()

    fun init() {
        clientGameStartEvent.listen { _ ->
            onStart()
        }
        ClientTickEvents.END_WORLD_TICK.register(this)
    }

    private fun handleWallGeneration(player: ClientPlayerEntity) {
        val leftOffset = 4.0
        val rightOffset = -20.0
        val offset = 1.0
        val railOffset = 3.5
        leftWallPatternGenerator = PatternGenerator(
            startPos = ClientSettings.startPos!!.add(leftOffset + offset, -1.0, 0.0).toBlockPos(),
            patternStack = Stack<Stack<String>>().apply { add(ClientSettings.getLeftPattern().toStack()) }
        )
        railPatternGenerator = PatternGenerator(
            startPos = ClientSettings.startPos!!.add(-railOffset, -1.0, 0.0).toBlockPos(),
            patternStack = Stack<Stack<String>>().apply { add(ClientSettings.getMiddlePattern().toStack()) }
        )
        rightWallPatternGenerator = PatternGenerator(
            startPos = ClientSettings.startPos!!.add(rightOffset - offset, -1.0, 0.0).toBlockPos(),
            patternStack = Stack<Stack<String>>().apply { add(ClientSettings.getRightPattern().toStack()) },
            mirror = BlockMirror.FRONT_BACK
        )
    }

    private fun onStart() {
        val player = MinecraftClient.getInstance().player ?: return
        clearFakeBlocksAndEntities(true)
        handleWallGeneration(player)
    }

    fun clearFakeBlocksAndEntities(forceClear: Boolean = false) {
        mcCoroutineTask(sync = true, client = true) {
            val player = MinecraftClient.getInstance().player ?: return@mcCoroutineTask

            val toRemove = mutableListOf<BlockInfo>()
            for (fakeBlock in fakeBlocks) {
                val shouldClear = forceClear || fakeBlock.pos.z < player.z - 5
                if (shouldClear) {
                    player.world.setBlockState(fakeBlock.pos, Blocks.AIR.defaultState, Block.NOTIFY_ALL_AND_REDRAW)
                    toRemove.add(fakeBlock)
                }
            }
            if (forceClear) {
                val entityRemoval = mutableListOf<Entity>()
                for (entity in (player.world as WorldAccessor).invokeGetEntityLookup().iterate()) {
                    if (entity is UUIDMarker && entity.owner == player.uuid) {
                        entityRemoval.add(entity)
                    }
                }
                entityRemoval.forEach(Entity::discard)
            }
            fakeBlocks.removeAll(toRemove.toSet())
        }
    }

    override fun onEndTick(world: ClientWorld) {
        val player = MinecraftClient.getInstance().player ?: return
        if (player.isSubwaySurfersOrSpectator) {
            clearFakeBlocksAndEntities()
            leftWallPatternGenerator?.tick(player)
            railPatternGenerator?.tick(player)
            rightWallPatternGenerator?.tick(player)
        }
    }
}
