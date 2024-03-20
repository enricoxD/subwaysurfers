package gg.norisk.subwaysurfers.mixin.entity;

import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurferKt;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
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
}
