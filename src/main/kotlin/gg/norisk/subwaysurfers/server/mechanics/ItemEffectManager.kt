package gg.norisk.subwaysurfers.server.mechanics

import gg.norisk.subwaysurfers.network.c2s.coinCollisionPacketC2S
import gg.norisk.subwaysurfers.network.c2s.jetpackCollisionPacketC2S
import gg.norisk.subwaysurfers.network.c2s.magnetCollisionPacketC2S
import gg.norisk.subwaysurfers.registry.ItemRegistry
import gg.norisk.subwaysurfers.subwaysurfers.coins
import gg.norisk.subwaysurfers.subwaysurfers.hasJetpack
import gg.norisk.subwaysurfers.subwaysurfers.isMagnetic
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import kotlinx.coroutines.Job
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.silkmc.silk.core.task.mcCoroutineTask
import java.util.*
import kotlin.time.Duration.Companion.seconds

object ItemEffectManager {
    private val magnetMap = mutableMapOf<UUID, Job>()
    private val jetpackMap = mutableMapOf<UUID, Job>()

    //TODO position check
    fun init() {
        magnetCollisionPacketC2S.receiveOnServer { packet, context ->
            val player = context.player
            if (player.isSubwaySurfers) {
                magnetMap[player.uuid]?.cancel()

                player.isMagnetic = true

                magnetMap[player.uuid] = mcCoroutineTask(delay = 12.seconds) {
                    player.isMagnetic = false
                    magnetMap.remove(player.uuid)
                }
            }
        }

        jetpackCollisionPacketC2S.receiveOnServer { packet, context ->
            val player = context.player
            if (player.isSubwaySurfers) {
                jetpackMap[player.uuid]?.cancel()

                player.hasJetpack = true
                player.inventory.armor[2] = ItemStack(ItemRegistry.JETPACK)
                player.inventory.updateItems()
                player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)?.let {
                    it.baseValue *= 2
                }

                jetpackMap[player.uuid] = mcCoroutineTask(delay = 8.seconds) {
                    player.hasJetpack = false
                    player.inventory.armor[2] = ItemStack.EMPTY
                    player.inventory.updateItems()
                    jetpackMap.remove(player.uuid)

                    player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)?.let {
                        it.baseValue /= 2
                    }
                }
            }
        }

        coinCollisionPacketC2S.receiveOnServer { packet, context ->
            val player = context.player
            if (player.isSubwaySurfers) {
                player.coins++
                player.playSound(
                    SoundEvents.ENTITY_ITEM_PICKUP,
                    SoundCategory.PLAYERS,
                    0.5f,
                    3f
                )
            }
        }
    }
}