package gg.norisk.subwaysurfers.entity

import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import software.bernie.geckolib.animatable.GeoEntity
import software.bernie.geckolib.constant.DefaultAnimations
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.core.animation.AnimatableManager
import software.bernie.geckolib.util.GeckoLibUtil

class TrafficLightEntity(type: EntityType<out AnimalEntity>, level: World) : DriveableEntity(type, level), GeoEntity{
    private val cache: AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this)

    override fun pushAwayFrom(entity: Entity?) {
    }

    override fun pushAway(entity: Entity?) {
    }

    override fun getBodyYaw(): Float = 180f
    override fun getHeadYaw(): Float = 180f
    override fun getYaw(): Float = 180f

    // Turn off step sounds since it's a bike
    override fun playStepSound(pos: BlockPos, block: BlockState) {}

    // Add our generic idle animation controller
    override fun registerControllers(controllers: AnimatableManager.ControllerRegistrar) {
        controllers.add(DefaultAnimations.genericIdleController(this))
    }

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache = this.cache
}
