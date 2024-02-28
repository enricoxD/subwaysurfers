package gg.norisk.subwaysurfers.client.lifecycle

import gg.norisk.subwaysurfers.client.lifecycle.ClientGameStartLifeCycle.leftWallPatternGenerator
import gg.norisk.subwaysurfers.client.lifecycle.ClientGameStartLifeCycle.railPatternGenerator
import gg.norisk.subwaysurfers.client.lifecycle.ClientGameStartLifeCycle.rightWallPatternGenerator
import gg.norisk.subwaysurfers.entity.UUIDMarker
import gg.norisk.subwaysurfers.network.c2s.gameStopPacketC2S
import net.minecraft.client.MinecraftClient
import net.silkmc.silk.core.event.Event

object ClientGameStopLifeCycle {
    val clientGameStopEvent = Event.onlySync<Unit>()

    fun init() {
        clientGameStopEvent.listen {
            gameStopPacketC2S.send(Unit)

            leftWallPatternGenerator = null
            rightWallPatternGenerator = null
            railPatternGenerator = null
            val player = MinecraftClient.getInstance().player ?: return@listen

            for (entity in player.clientWorld.entities) {
                if (entity is UUIDMarker && entity.owner == player.uuid) {
                    entity.discard()
                }
            }
        }
    }
}
