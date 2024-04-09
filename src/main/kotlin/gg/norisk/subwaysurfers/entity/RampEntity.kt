package gg.norisk.subwaysurfers.entity

import gg.norisk.subwaysurfers.client.lifecycle.ClientGameRunningLifeCycle
import gg.norisk.subwaysurfers.registry.BlockRegistry
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.silkmc.silk.core.world.block.BlockInfo
import software.bernie.geckolib.animatable.GeoEntity
import software.bernie.geckolib.constant.DefaultAnimations
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar
import software.bernie.geckolib.util.GeckoLibUtil

class RampEntity(type: EntityType<out AnimalEntity>, level: World) : DriveableEntity(type, level), GeoEntity {
    private val cache: AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this)

    override fun pushAwayFrom(entity: Entity?) {
    }

    override fun pushAway(entity: Entity?) {
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

    override fun isLogicalSideForUpdatingMovement(): Boolean = true
    override fun isCollidable(): Boolean = false

    // Turn off step sounds since it's a bike
    override fun playStepSound(pos: BlockPos, block: BlockState) {}

    // Add our generic idle animation controller
    override fun registerControllers(controllers: ControllerRegistrar) {
        controllers.add(DefaultAnimations.genericIdleController(this))
    }

    fun placeStairs() {
        if (world.isClient || FabricLoader.getInstance().isDevelopmentEnvironment) {
            val xOffset = 0
            val firstBlockPos = BlockPos(this.blockX + xOffset, this.blockY, this.blockZ - 2)
            val baseBlock = Blocks.BARRIER
            val baseSlab = BlockRegistry.BARRIER_SLAB
            world.setBlockState(
                firstBlockPos.apply {
                    ClientGameRunningLifeCycle.fakeBlocks.add(BlockInfo(Blocks.AIR.defaultState, this))
                },
                baseSlab.defaultState
            )
            world.setBlockState(
                BlockPos(this.blockX + xOffset, this.blockY, this.blockZ - 1).apply {
                    ClientGameRunningLifeCycle.fakeBlocks.add(BlockInfo(Blocks.AIR.defaultState, this))
                },
                baseBlock.defaultState
            )
            world.setBlockState(
                BlockPos(this.blockX + xOffset, this.blockY + 1, this.blockZ).apply {
                    ClientGameRunningLifeCycle.fakeBlocks.add(BlockInfo(Blocks.AIR.defaultState, this))
                },
                baseSlab.defaultState
            )
            world.setBlockState(
                BlockPos(this.blockX + xOffset, this.blockY + 1, this.blockZ + 1).apply {
                    ClientGameRunningLifeCycle.fakeBlocks.add(BlockInfo(Blocks.AIR.defaultState, this))
                },
                baseBlock.defaultState
            )
            world.setBlockState(
                BlockPos(this.blockX + xOffset, this.blockY + 2, this.blockZ + 2).apply {
                    ClientGameRunningLifeCycle.fakeBlocks.add(BlockInfo(Blocks.AIR.defaultState, this))
                },
                baseSlab.defaultState
            )
            world.setBlockState(
                BlockPos(this.blockX + xOffset, this.blockY + 2, this.blockZ + 3).apply {
                    ClientGameRunningLifeCycle.fakeBlocks.add(BlockInfo(Blocks.AIR.defaultState, this))
                },
                baseBlock.defaultState
            )
        }
    }

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache = this.cache
}
