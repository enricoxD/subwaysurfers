package gg.norisk.subwaysurfers.mixin.client.render.entity;

import gg.norisk.subwaysurfers.client.renderer.entity.PoliceRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IronGolemEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolemEntityRenderer.class)
public abstract class IronGolemEntityRendererMixin extends MobEntityRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {
    public IronGolemEntityRendererMixin(EntityRendererFactory.Context context, IronGolemEntityModel<IronGolemEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "getTexture(Lnet/minecraft/entity/passive/IronGolemEntity;)Lnet/minecraft/util/Identifier;", at = @At("RETURN"), cancellable = true)
    private void injected(IronGolemEntity ironGolemEntity, CallbackInfoReturnable<Identifier> cir) {
        PoliceRenderer.INSTANCE.handlePoliceTexture(ironGolemEntity, cir);
    }
}
