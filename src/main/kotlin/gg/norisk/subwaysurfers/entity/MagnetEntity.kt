package gg.norisk.subwaysurfers.entity

import gg.norisk.subwaysurfers.entity.TrainEntity.Companion.handleDiscard
import gg.norisk.subwaysurfers.network.c2s.magnetCollisionPacketC2S
import gg.norisk.subwaysurfers.network.dto.toDto
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import net.minecraft.block.BlockState
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

class MagnetEntity(type: EntityType<out AnimalEntity>, level: World) : AnimalEntity(type, level), GeoEntity, OriginMarker,
    UUIDMarker {
    private val cache: AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this)
    override var owner: UUID? = null
    override var origin: BlockPos = this.blockPos

    init {
        this.ignoreCameraFrustum = true
    }

    override fun tick() {
        super.tick()
        handleDiscard(owner)
    }

    override fun onPlayerCollision(player: PlayerEntity) {
        if (world.isClient && player.isSubwaySurfers) {
            magnetCollisionPacketC2S.send(this.blockPos.toDto())
            this.discard()
        }
    }

    // Turn off step sounds since it's a bike
    override fun playStepSound(pos: BlockPos, block: BlockState) {}

    // Apply player-controlled movement
    override fun travel(pos: Vec3d) {
    }

    // Add our generic idle animation controller
    override fun registerControllers(controllers: ControllerRegistrar) {
        controllers.add(DefaultAnimations.genericIdleController(this))
    }

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache = this.cache
    override fun isLogicalSideForUpdatingMovement(): Boolean = true
    override fun createChild(level: ServerWorld, partner: PassiveEntity): PassiveEntity? = null
}
