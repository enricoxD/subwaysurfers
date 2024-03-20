package gg.norisk.subwaysurfers.common.item

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.client.model.entity.CollectibleModel
import gg.norisk.subwaysurfers.common.entity.CollectibleEntity
import gg.norisk.subwaysurfers.item.PowerupItem
import gg.norisk.subwaysurfers.registry.EntityRegistry
import gg.norisk.subwaysurfers.registry.ItemRegistry
import gg.norisk.subwaysurfers.subwaysurfers.coins
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.item.*
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.silkmc.silk.core.entity.directionVector
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.text.literal
import software.bernie.geckolib.renderer.GeoEntityRenderer
import kotlin.time.Duration.Companion.seconds

val magnet = object: Powerup("magnet", 10.seconds, ArmorItem.Type.CHESTPLATE /* todo hold magnet in hand */) {
    override fun onPickupServer(player: ServerPlayerEntity) {
        super.onPickupServer(player)

        // ServerPlayer pickup up a magnet
        player.playSound(SoundEvents.ENTITY_CAT_EAT, SoundCategory.PLAYERS, 0.5f, 3f)
    }

    override fun onTickServer(player: ServerPlayerEntity) {
        for (coin in player.world.getEntitiesByClass(CollectibleEntity::class.java, player.boundingBox.expand(5.0)) {
            it.type == coin.entityType // check if it's a coin
        }) {
            val direction =
                player.eyePos.add(player.directionVector.normalize().multiply(2.0)).subtract(coin.pos).normalize().multiply(0.9)

            coin.modifyVelocity(direction)
            if (coin.distanceTo(player) < 2) {
                coin.onPlayerCollision(player)
            }
        }
    }
}

val boots = Powerup("boots", 8.seconds, ArmorItem.Type.LEGGINGS)
val jetpack = object : Powerup("jetpack", 5.seconds, ArmorItem.Type.CHESTPLATE) {
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
}

val hoverboard = Powerup("hoverboard", 10.seconds, ArmorItem.Type.BOOTS /* ?? */)
val coin = object : Collectible("coin") {
    override fun onPickupServer(player: ServerPlayerEntity) {
        super.onPickupServer(player)

        // ServerPlayer pickup up a coin
        player.coins++
        player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.5f, 3f)
    }
}

val collectibles = listOf(coin, jetpack, hoverboard, magnet, boots)

object Collectibles {
    fun register() {
        // Register entities, etc. for all collectibles/powerups
        val items = mutableListOf<Item>()

        collectibles.forEach { collectible ->
            // Entity
            collectible.entityType = EntityRegistry.registerMob(
                collectible.id,
                { type, world -> CollectibleEntity(collectible, type, world) },
                1f,
                1f
            )
            // Attributes
            FabricDefaultAttributeRegistry.register(collectible.entityType, EntityRegistry.createGenericEntityAttributes())

            // Item
            val item = if (collectible is Powerup) {
                PowerupItem(collectible, Item.Settings())
            } else {
                Item(Item.Settings())
            }
            collectible.item = ItemRegistry.registerItem(collectible.id, item)
            items.add(collectible.item!!)
        }

        // Item group
        Registry.register(
            Registries.ITEM_GROUP, "subwaysurfers_collectibles".toId(), FabricItemGroup
                .builder()
                .displayName(Text.translatable("itemGroup.subwaysurfers.subwaysurfers_collectibles"))
                .icon { ItemStack(items.randomOrNull() ?: Items.AIR) }
                .entries { _: ItemGroup.DisplayContext?, entries: ItemGroup.Entries ->
                    items.map(::ItemStack).forEach(entries::add)
                }.build()
        )
    }

    fun registerClient() {
        collectibles.forEach { collectible ->
            // Renderer (only for client-side)
            val entityType = collectible.entityType ?: return@forEach
            EntityRendererRegistry.register(entityType) {
                GeoEntityRenderer(it, CollectibleModel(collectible.id.toId()))
            }
        }
    }
}