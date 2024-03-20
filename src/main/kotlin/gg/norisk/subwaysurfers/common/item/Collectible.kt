@file:OptIn(ExperimentalSerializationApi::class)

package gg.norisk.subwaysurfers.common.item

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.common.entity.CollectibleEntity
import gg.norisk.subwaysurfers.network.dto.BlockPosDto
import gg.norisk.subwaysurfers.registry.EntityRegistry
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import kotlinx.serialization.ExperimentalSerializationApi
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.item.ArmorItem
import net.minecraft.item.Item
import net.minecraft.server.network.ServerPlayerEntity
import net.silkmc.silk.network.packet.c2sPacket

/**
 * A [Collectible] is an item (in minecraft terms an [Entity]) that can be picked up by the player; a power-up or a collectible.
 *
 * Example collectibles: [coin], key
 *
 * Example [Powerup]s: [magnet], [jetpack], [hoverboard]
 */
open class Collectible(
    /** Unique identifier for linking resources like model, texture and animation. */
    val id: String,
) {
    /** packet that is sent from a client to the server to indicate that the item was picked up */
    val pickupPacket = c2sPacket<BlockPosDto>("item_${id}_pickup".toId())

    open val armorType: ArmorItem.Type? = null

    init {
        pickupPacket.receiveOnServer { _, context ->
            // TODO position check; currently we just assume that the item exists and the player really picked it up

            context.player.takeIf { it.isSubwaySurfers }?.let(::onPickupServer)
        }
    }

    /** Callback called on the **server** when the client notified the server about picking up the item. */
    open fun onPickupServer(player: ServerPlayerEntity) {}

    // ----------

    var item: Item? = null

    var entityType: EntityType<CollectibleEntity>? = null
}