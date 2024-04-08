package gg.norisk.subwaysurfers.client.renderer.entity

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.mixin.entity.LimbAnimatorAccessor
import gg.norisk.subwaysurfers.mixin.entity.LivingEntityAccessor
import gg.norisk.subwaysurfers.network.s2c.policeTeleportPacketS2C
import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurfer
import gg.norisk.subwaysurfers.subwaysurfers.punishTicks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.IronGolemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.silkmc.silk.core.text.literal
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable


object PoliceRenderer {
    private val IRON_GOLEM_POLICE_TEXTURE = "textures/entity/police_iron_golem.png".toId()

    private val Entity.renderer
        get() = MinecraftClient.getInstance().entityRenderDispatcher.getRenderer(this)

    fun init() {
        policeTeleportPacketS2C.receiveOnClient { _, context ->
            val player = context.client.player ?: return@receiveOnClient
            val subwaySurfer = player as? SubwaySurfer? ?: return@receiveOnClient
            player.lerpedPolicePosition = (player.punishTicks / 90f)
        }
    }

    fun render(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        headYaw: Float,
        tickDelta: Float,
        player: PlayerEntity,
        i: Int,
    ) {
        val subwaySurfer = player as SubwaySurfer ?: return
        if (player.punishTicks > 0) {
            matrices.push()
            val speedMultiplier = (1f / (player.punishTicks / 30f))
            player.lerpedPolicePosition = MathHelper.lerp(
                tickDelta * 0.5f,
                player.lerpedPolicePosition,
                speedMultiplier - player.punishTicks / 90f
            )
            matrices.translate(0f, 0f, -2f)
            matrices.translate(0f, 0f, 0.5f - player.lerpedPolicePosition)

            val fakeEntity = EntityType.IRON_GOLEM.create(player.world) ?: return
            fakeEntity.customName = "SubwaySurfersPolice".literal
            val entityRenderer = fakeEntity.renderer ?: return

            fakeEntity.copyPlayerAttributes(player)
            entityRenderer.render(fakeEntity, headYaw, tickDelta, matrices, vertexConsumers, light)

            renderPoliceDog(matrices, vertexConsumers, light, headYaw, tickDelta, player, i)

            matrices.pop()
        }
    }

    private fun renderPoliceDog(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        headYaw: Float,
        tickDelta: Float,
        player: PlayerEntity,
        i: Int,
    ) {
        matrices.translate(+1.2f, 0f, 0f)
        val dog = EntityType.WOLF.create(player.world) ?: return
        val dogRenderer = dog.renderer ?: return

        dog.copyPlayerAttributes(player)
        dogRenderer.render(dog, headYaw, tickDelta, matrices, vertexConsumers, light)
    }

    private fun Entity.copyPlayerAttributes(player: PlayerEntity) {
        yaw = player.yaw
        prevYaw = player.prevYaw
        prevPitch = player.prevPitch
        pitch = player.pitch
        age = player.age
        isSneaking = player.isSneaking
        velocity = player.velocity
        isOnFire = player.isOnFire
        pose = player.pose
        val dummy1 = this as? LivingEntity ?: return
        val dummy2 = this as? LivingEntityAccessor ?: return
        val dummy3 = player as? LivingEntityAccessor ?: return
        lastLeaningPitch = player.lastLeaningPitch
        leaningPitch = player.leaningPitch
        prevBodyYaw = player.prevBodyYaw
        prevHeadYaw = player.prevHeadYaw
        val target = limbAnimator as LimbAnimatorAccessor
        val source = player.limbAnimator as LimbAnimatorAccessor
        target.prevSpeed = source.prevSpeed
        target.speed = source.speed
        target.pos = source.pos
        handSwingProgress = player.handSwingProgress
        handSwingTicks = player.handSwingTicks
        handSwinging = player.handSwinging
        lastHandSwingProgress = player.lastHandSwingProgress
        hurtTime = player.hurtTime
        bodyYaw = player.bodyYaw
        headYaw = player.headYaw
        isOnGround = player.isOnGround
    }

    fun handlePoliceTexture(ironGolemEntity: IronGolemEntity, cir: CallbackInfoReturnable<Identifier>) {
        if (ironGolemEntity.customName == "SubwaySurfersPolice".literal) {
            cir.returnValue = IRON_GOLEM_POLICE_TEXTURE
        }
    }
}
