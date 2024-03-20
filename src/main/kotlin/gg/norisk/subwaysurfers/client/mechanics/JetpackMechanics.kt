package gg.norisk.subwaysurfers.client.mechanics

import gg.norisk.subwaysurfers.subwaysurfers.hasJetpack
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.particle.ParticleTypes

object JetpackMechanics {
    fun init() {
        ClientTickEvents.START_CLIENT_TICK.register { client ->
            val player = client.player ?: return@register
            if (player.hasJetpack) {
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
        }
    }
}