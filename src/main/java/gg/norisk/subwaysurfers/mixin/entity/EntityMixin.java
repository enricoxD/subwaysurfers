package gg.norisk.subwaysurfers.mixin.entity;

import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurferKt;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    private EntityDimensions dimensions;
  
    @Shadow public abstract void setPos(double d, double e, double f);

    @Shadow public abstract void setBoundingBox(Box box);

    @Shadow protected abstract Box calculateBoundingBox();

    @Inject(method = "setPosition(DDD)V", at = @At("HEAD"), cancellable = true)
    public void setPosition(double x, double y, double z, CallbackInfo ci) {
        if ((Object) this instanceof ClientPlayerEntity player) {
            if (SubwaySurferKt.isSubwaySurfers(player) && SubwaySurferKt.getHasJetpack(player)) {
                ci.cancel();
                setPos(x, SubwaySurferKt.getJetpackY(player, y), z);
                setBoundingBox(calculateBoundingBox());
            }
        }
    }

    @Redirect(method = "calculateDimensions", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;dimensions:Lnet/minecraft/entity/EntityDimensions;", opcode = Opcodes.PUTFIELD))
    private void injected(Entity instance, EntityDimensions value) {
        if (instance instanceof PlayerEntity player && SubwaySurferKt.isSliding(player)) {
            dimensions = EntityDimensions.fixed(0.2f, 0.2f);
        } else {
            dimensions = value;
        }
    }
}
