package gg.norisk.subwaysurfers.mixin.client.render.entity;

import gg.norisk.subwaysurfers.client.renderer.entity.PoliceRenderer;
import gg.norisk.subwaysurfers.registry.ItemRegistry;
import gg.norisk.subwaysurfers.SubwaySurfers;
import gg.norisk.subwaysurfers.common.collectible.Hoverboard;
import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurferKt;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public PlayerEntityRendererMixin(EntityRendererFactory.Context context, PlayerEntityModel<AbstractClientPlayerEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "getPositionOffset(Lnet/minecraft/client/network/AbstractClientPlayerEntity;F)Lnet/minecraft/util/math/Vec3d;", at = @At("RETURN"), cancellable = true)
    private void injected(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, CallbackInfoReturnable<Vec3d> cir) {
        if (Hoverboard.INSTANCE.isActiveFor(abstractClientPlayerEntity)) {
            Vec3d returnValue = cir.getReturnValue();
            cir.setReturnValue(returnValue.add(0.0, 0.5, 0.0));
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private void injected(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        PoliceRenderer.INSTANCE.render(matrixStack, vertexConsumerProvider, i, f, g, abstractClientPlayerEntity, i);
    }
}
