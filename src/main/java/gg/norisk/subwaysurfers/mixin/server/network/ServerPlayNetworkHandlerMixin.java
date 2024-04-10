package gg.norisk.subwaysurfers.mixin.server.network;

import gg.norisk.subwaysurfers.server.command.StructureCommand;
import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurferKt;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(method = "onUpdateStructureBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendMessage(Lnet/minecraft/text/Text;Z)V", shift = At.Shift.AFTER, ordinal = 0))
    private void onUpdateStructureBlockSaveInjection(UpdateStructureBlockC2SPacket updateStructureBlockC2SPacket, CallbackInfo ci) {
        if (this.player.getWorld().getBlockEntity(updateStructureBlockC2SPacket.getPos()) instanceof StructureBlockBlockEntity structureBlockEntity) {
            StructureCommand.INSTANCE.handleStructureBlockSaveMessage(player, structureBlockEntity.getTemplateName());
        }
    }
}
