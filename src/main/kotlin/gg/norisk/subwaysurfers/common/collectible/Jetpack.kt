package gg.norisk.subwaysurfers.common.collectible

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.item.ArmorItem
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import kotlin.time.Duration.Companion.seconds

object Jetpack  : Powerup("jetpack", 5.seconds, ArmorItem.Type.CHESTPLATE) {
    override fun onTickClient(player: ClientPlayerEntity) {
        // add particles
        repeat(2) {
            player.world.addParticle(
                ParticleTypes.FLAME,
                player.x + 0.1 - it * 0.2,
                player.y + 0.6,
                player.z + 0.1,
                0.0,
                0.0,
                0.0
            )
        }
    }

    override fun onTickServer(player: ServerPlayerEntity) {
        super.onTickServer(player)
    }
}