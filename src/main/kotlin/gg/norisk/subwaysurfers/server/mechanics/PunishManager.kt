package gg.norisk.subwaysurfers.server.mechanics

import gg.norisk.subwaysurfers.network.c2s.blockCollisionPacketC2S
import gg.norisk.subwaysurfers.network.c2s.horizontalCollisionPacketC2S
import gg.norisk.subwaysurfers.network.c2s.punishPacketC2S
import gg.norisk.subwaysurfers.server.command.StartCommand
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import gg.norisk.subwaysurfers.subwaysurfers.punishTicks
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.silkmc.silk.core.text.literal

object PunishManager {
    fun init() {
        punishPacketC2S.receiveOnServer { packet, context ->
            val player = context.player
            if (player.isSubwaySurfers) {
                player.punishHit()
            }
        }
        horizontalCollisionPacketC2S.receiveOnServer { packet, context ->
            val player = context.player
            if (player.isSubwaySurfers) {
                StartCommand.handleGameStop(player)
            }
        }
        blockCollisionPacketC2S.receiveOnServer { packet, context ->
            val player = context.player
            if (player.isSubwaySurfers) {
                player.punishHit()
            }
        }
    }

    /** Punish player for dashing against train or wall */
    fun ServerPlayerEntity.punishHit() {
        world.playSoundFromEntity(
            null,
            this,
            SoundEvents.ENTITY_PLAYER_HURT,
            SoundCategory.PLAYERS,
            0.4f,
            0.8f
        )
        punishTicks += 20 * 3
        checkGameOver()
    }

    /** check for potential loss, depending on current punish ticks */
    private fun ServerPlayerEntity.checkGameOver() {
        if (punishTicks > 70) {
            StartCommand.handleGameStop(this)
        }
    }
}