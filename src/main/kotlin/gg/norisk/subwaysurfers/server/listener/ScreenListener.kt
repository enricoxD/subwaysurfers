package gg.norisk.subwaysurfers.server.listener

import gg.norisk.subwaysurfers.network.c2s.homePacketC2S
import gg.norisk.subwaysurfers.network.c2s.restartPacketC2S
import gg.norisk.subwaysurfers.server.ServerConfig
import gg.norisk.subwaysurfers.server.command.StartCommand
import net.minecraft.sound.SoundEvents

object ScreenListener {
    fun init() {
        restartPacketC2S.receiveOnServer { packet, context ->
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
            //TODO wieso geht der scheiß sound nicht hö
            player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 0.8f, 1f)
        }
    }
}
