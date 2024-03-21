package gg.norisk.subwaysurfers.entity

import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import software.bernie.geckolib.animatable.GeoEntity
import software.bernie.geckolib.constant.DefaultAnimations
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar
import software.bernie.geckolib.util.GeckoLibUtil

class RampEntity(type: EntityType<out AnimalEntity>, level: World) : DriveableEntity(type, level), GeoEntity {
    private val cache: AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this)

    override fun initDataTracker() {
        super.initDataTracker()
    }

    override fun writeCustomDataToNbt(nbtCompound: NbtCompound) {
        super.writeCustomDataToNbt(nbtCompound)
    }

    override fun readCustomDataFromNbt(nbtCompound: NbtCompound) {
        super.readCustomDataFromNbt(nbtCompound)
    }

    override fun getBodyYaw(): Float = 180f
    override fun getHeadYaw(): Float = 180f
    override fun getYaw(): Float = 180f

    override fun collidesWith(entity: Entity): Boolean {
        if (entity is RampEntity || entity is PlayerEntity) {
            return false
        }
        return super.collidesWith(entity)
    }

    override fun onPlayerCollision(playerEntity: PlayerEntity) {
        super.onPlayerCollision(playerEntity)
        if (world.isClient) {
            playerEntity.setPosition(playerEntity.x, playerEntity.y + 2, playerEntity.z)
        }
    }

    override fun isLogicalSideForUpdatingMovement(): Boolean = true
    override fun isCollidable(): Boolean = true

    // Turn off step sounds since it's a bike
    override fun playStepSound(pos: BlockPos, block: BlockState) {}

    // Add our generic idle animation controller
    override fun registerControllers(controllers: ControllerRegistrar) {
        controllers.add(DefaultAnimations.genericIdleController(this))
    }

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache = this.cache
}
