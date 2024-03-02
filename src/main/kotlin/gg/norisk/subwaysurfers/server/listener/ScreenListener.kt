package gg.norisk.subwaysurfers.server.listener

import gg.norisk.subwaysurfers.network.c2s.homePacketC2S
import gg.norisk.subwaysurfers.network.c2s.restartPacketC2S
import gg.norisk.subwaysurfers.server.ServerConfig
import gg.norisk.subwaysurfers.server.command.StartCommand
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents

object ScreenListener {
    fun init() {
        restartPacketC2S.receiveOnServer { packet, context ->
            val player = context.player
            StartCommand.handleStartGame(context.player)
        }
        homePacketC2S.receiveOnServer { packet, context ->
            val player = context.player
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
    }
}
