package gg.norisk.subwaysurfers.entity

import gg.norisk.subwaysurfers.entity.TrainEntity.Companion.handleDiscard
import gg.norisk.subwaysurfers.network.c2s.coinCollisionPacketC2S
import gg.norisk.subwaysurfers.network.dto.toDto
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import software.bernie.geckolib.animatable.GeoEntity
import software.bernie.geckolib.constant.DefaultAnimations
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar
import software.bernie.geckolib.util.GeckoLibUtil
import java.util.*

class CoinEntity(type: EntityType<out AnimalEntity>, level: World) : AnimalEntity(type, level), GeoEntity, UUIDMarker, OriginMarker {
    override var owner: UUID? = null
    override var origin: BlockPos = this.blockPos
    private val cache: AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this)

    init {
        this.ignoreCameraFrustum = true
    }

    override fun tick() {
        super.tick()
        handleDiscard(owner)
    }

    override fun pushAwayFrom(entity: Entity?) {
    }

    override fun pushAway(entity: Entity?) {
    }

    override fun onPlayerCollision(player: PlayerEntity) {
        if (world.isClient) {
            coinCollisionPacketC2S.send(this.origin.toDto())
            this.discard()
        }
    }

    // Turn off step sounds since it's a bike
    override fun playStepSound(pos: BlockPos, block: BlockState) {}

    // Apply player-controlled movement
    override fun travel(pos: Vec3d) {
    }

    override fun isLogicalSideForUpdatingMovement(): Boolean = true

    // Add our generic idle animation controller
    override fun registerControllers(controllers: ControllerRegistrar) {
        controllers.add(DefaultAnimations.genericIdleController(this))
    }

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache = this.cache
    override fun createChild(level: ServerWorld, partner: PassiveEntity): PassiveEntity? = null
}
