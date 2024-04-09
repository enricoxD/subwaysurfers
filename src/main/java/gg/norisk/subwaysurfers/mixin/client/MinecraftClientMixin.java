package gg.norisk.subwaysurfers.mixin.client;

import gg.norisk.subwaysurfers.event.events.WorldEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "joinWorld", at = @At("HEAD"))
    private void injected(ClientWorld clientWorld, CallbackInfo ci) {
        WorldEvents.INSTANCE.getClientJoinWorldEvent().invoke(new WorldEvents.WorldEvent(clientWorld));
    }
}
