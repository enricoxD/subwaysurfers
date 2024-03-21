package gg.norisk.subwaysurfers.server.listener

import gg.norisk.subwaysurfers.entity.DriveableEntity
import gg.norisk.subwaysurfers.entity.TrainEntity
import gg.norisk.subwaysurfers.network.c2s.homePacketC2S
import gg.norisk.subwaysurfers.network.c2s.nbtChangePacketC2S
import gg.norisk.subwaysurfers.network.c2s.restartPacketC2S
import gg.norisk.subwaysurfers.server.ServerConfig
import gg.norisk.subwaysurfers.server.command.StartCommand
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.silkmc.silk.core.text.literal

object ScreenListener {
    fun init() {
        restartPacketC2S.receiveOnServer { packet, context ->
            val player = context.player
            StartCommand.handleStartGame(context.player)
        }
        homePacketC2S.receiveOnServer { packet, context ->
            val player = context.player
            player.isSubwaySurfers = false
            player.teleport(
                player.serverWorld,
                ServerConfig.config.spawn.x,
                ServerConfig.config.spawn.y,
                ServerConfig.config.spawn.z,
                ServerConfig.config.spawn.yaw,
                ServerConfig.config.spawn.pitch,
            )
            player.world.playSoundFromEntity(
                null,
                player,
                SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.PLAYERS,
                0.4f,
                0.8f
            )
        }
        nbtChangePacketC2S.receiveOnServer { packet, context ->
            val player = context.player
            if (player.isCreativeLevelTwoOp) {
                val entity = player.serverWorld.getEntity(packet.uuid) ?: return@receiveOnServer
                if (entity is DriveableEntity) {
                    if (packet.shouldDrive != null) entity.shouldDrive = packet.shouldDrive
                    if (packet.moveSpeed != null) entity.moveSpeed = packet.moveSpeed
                }
                if (entity is TrainEntity) {
                    if (packet.variant != null) entity.variation = packet.variant
                }
                player.sendMessage("Saved NBT Changes".literal)
            }
        }
    }
}
