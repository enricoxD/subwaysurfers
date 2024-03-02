package gg.norisk.subwaysurfers.entity

import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import software.bernie.geckolib.animatable.GeoEntity
import software.bernie.geckolib.constant.DefaultAnimations
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar
import software.bernie.geckolib.util.GeckoLibUtil
import java.util.*

class TrainEntity(type: EntityType<out AnimalEntity>, level: World) : DriveableEntity(type, level), GeoEntity, UUIDMarker {
    private val cache: AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this)
    override var owner: UUID? = null

    var variation: Int
        get() {
            return this.dataTracker.get(TYPE)
        }
        set(value) {
            this.dataTracker.set(TYPE, value)
        }

    override fun initDataTracker() {
        super.initDataTracker()
        this.dataTracker.startTracking(TYPE, 1)
    }

    override fun writeCustomDataToNbt(nbtCompound: NbtCompound) {
        super.writeCustomDataToNbt(nbtCompound)
        nbtCompound.putInt("Variation", variation)
    }

    override fun readCustomDataFromNbt(nbtCompound: NbtCompound) {
        super.readCustomDataFromNbt(nbtCompound)
        variation = nbtCompound.getInt("Variation")
    }

    override fun getBodyYaw(): Float = 180f
    override fun getHeadYaw(): Float = 180f
    override fun getYaw(): Float = 180f

    companion object {
        val TRAIN_TYPES = 2
        private val DRIVE: TrackedData<Boolean> =
            DataTracker.registerData(TrainEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        private val TYPE: TrackedData<Int> =
            DataTracker.registerData(TrainEntity::class.java, TrackedDataHandlerRegistry.INTEGER)

        fun LivingEntity.handleDiscard(owner: UUID?) {
            val player = world.getPlayerByUuid(owner ?: return)
            if (player == null || !player.isSubwaySurfers) {
                this.discard()
            } else {
                //TODO 5 should be a setting
                if (player.blockPos.z - 5 > this.blockPos.z) {
                    this.discard()
                }
            }
        }
    }

    override fun collidesWith(entity: Entity): Boolean {
        if (entity is TrainEntity) {
            return false
        }
        return super.collidesWith(entity)
    }

    override fun tick() {
        super.tick()
        handleDiscard(owner)
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
