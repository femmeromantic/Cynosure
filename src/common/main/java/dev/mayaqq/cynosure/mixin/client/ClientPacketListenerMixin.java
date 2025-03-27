package dev.mayaqq.cynosure.mixin.client;

import dev.mayaqq.cynosure.injection.ILevel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
public class ClientPacketListenerMixin {

    @Shadow private ClientLevel level;

    @Inject(
        method = "handleBlockUpdate",
        at = @At("TAIL")
    )
    public void handleBlockUpdate(ClientboundBlockUpdatePacket packet, CallbackInfo ci) {
        ((ILevel) this.level).cynosure_handleBlockUpdate(packet.getPos(), packet.getBlockState());
    }

    @Inject(
        method = "handleChunkBlocksUpdate",
        at = @At("TAIL")
    )
    public void handleBlockUpdates(ClientboundSectionBlocksUpdatePacket packet, CallbackInfo ci) {
        packet.runUpdates((pos, state) -> ((ILevel) this.level).cynosure_handleBlockUpdate(pos, state));
    }

}
