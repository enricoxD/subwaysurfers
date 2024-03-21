package gg.norisk.subwaysurfers.mixin.server.network;

import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurferKt;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    private boolean floating;

    @Shadow
    public ServerPlayerEntity player;

    @Redirect(method = "onPlayerMove", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;floating:Z", opcode = Opcodes.PUTFIELD))
    private void injected(ServerPlayNetworkHandler instance, boolean value) {
        floating = value && !SubwaySurferKt.isSubwaySurfers(player);
    }
}
