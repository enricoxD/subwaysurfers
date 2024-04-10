package gg.norisk.subwaysurfers.server.mechanics

import gg.norisk.subwaysurfers.common.collectible.Coin
import gg.norisk.subwaysurfers.common.collectible.Collectible
import gg.norisk.subwaysurfers.common.collectible.Magnet
import gg.norisk.subwaysurfers.network.dto.BlockPosDto
import gg.norisk.subwaysurfers.network.dto.toBlockPos
import gg.norisk.subwaysurfers.server.world.ServerRailPatternGenerator
import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurfer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.network.ServerPlayerEntity

object CollectibleManager {
    fun validatePickup(player: ServerPlayerEntity, position: BlockPosDto, collectible: Collectible): Boolean {
        if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            return true
        }
        val railPatternGenerator =
            (player as SubwaySurfer).railPatternGenerator as? ServerRailPatternGenerator ?: return false
        //TODO maybe needs adjustment
        val isWithinDistance = if (Magnet.isActiveFor(player) && collectible == Coin) {
            player.blockPos.isWithinDistance(position.toBlockPos(), 8.0)
        } else {
            player.blockPos.isWithinDistance(position.toBlockPos(), 2.0)
        }
        return railPatternGenerator.collectibles.any { it.origin == position.toBlockPos() && it.collectible == collectible } && isWithinDistance
    }
}
