package gg.norisk.subwaysurfers.server.listener

import gg.norisk.subwaysurfers.entity.UUIDMarker
import gg.norisk.subwaysurfers.network.c2s.gameStopPacketC2S
import gg.norisk.subwaysurfers.network.s2c.gameOverScreenS2C
import gg.norisk.subwaysurfers.server.mechanics.SpeedManager
import gg.norisk.subwaysurfers.subwaysurfers.coins
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import gg.norisk.subwaysurfers.subwaysurfers.rail
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.server.network.ServerPlayerEntity

object GameStopListener {
    fun init() {
        gameStopPacketC2S.receiveOnServer { packet, context ->
            if (context.player.isSubwaySurfers) {
                handleGameStop(context.player)
            }
        }
    }

    fun handleGameStop(player: ServerPlayerEntity) {
        gameOverScreenS2C.send(Unit, player)
        player.isSubwaySurfers = false
        player.coins = 0
        player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)?.baseValue = SpeedManager.vanillaSpeed
        player.rail = 1
        val entites = player.serverWorld.iterateEntities().toList()
        for (entity in entites) {
            if (entity is UUIDMarker && entity.owner == player.uuid) {
                entity.discard()
            }
        }
    }
}
