package gg.norisk.subwaysurfers.entity

import gg.norisk.subwaysurfers.entity.TrainEntity.Companion.handleDiscard
import gg.norisk.subwaysurfers.subwaysurfers.isMagnetic
import kotlinx.coroutines.Job
import net.minecraft.block.BlockState
import net.minecraft.entity.EntityType
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.silkmc.silk.core.task.mcCoroutineTask
import software.bernie.geckolib.animatable.GeoEntity
import software.bernie.geckolib.constant.DefaultAnimations
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar
import software.bernie.geckolib.util.GeckoLibUtil
import java.util.*
import kotlin.time.Duration.Companion.seconds

class MagnetEntity(type: EntityType<out AnimalEntity>, level: World) : AnimalEntity(type, level), GeoEntity,
    UUIDMarker {
    private val cache: AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this)
    override var owner: UUID? = null

    companion object {
        val magnetMap = mutableMapOf<UUID, Job>()
    }

    init {
        this.ignoreCameraFrustum = true
    }

    override fun tick() {
        super.tick()
        handleDiscard(owner)
    }

    override fun onPlayerCollision(player: PlayerEntity) {
        if (!world.isClient) {
            magnetMap[player.uuid]?.cancel()

            player.isMagnetic = true
            magnetMap[player.uuid] = mcCoroutineTask(delay = 5.seconds) {
                player.isMagnetic = false
                magnetMap.remove(player.uuid)
            }

            this.discard()
        }
    }

    // Turn off step sounds since it's a bike
    override fun playStepSound(pos: BlockPos, block: BlockState) {}

    // Apply player-controlled movement
    override fun travel(pos: Vec3d) {
    }

    override fun isLogicalSideForUpdatingMovement(): Boolean {
        return true
    }

    // Add our generic idle animation controller
    override fun registerControllers(controllers: ControllerRegistrar) {
        controllers.add(DefaultAnimations.genericIdleController(this))
    }

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache {
        return this.cache
    }

    override fun createChild(level: ServerWorld, partner: PassiveEntity): PassiveEntity? {
        return null
    }
}
