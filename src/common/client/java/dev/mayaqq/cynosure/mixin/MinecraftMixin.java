package dev.mayaqq.cynosure.mixin;

import dev.mayaqq.cynosure.Cynosure;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "onGameLoadFinished", at = @At("HEAD"))
    private void onGameLoadFinished(CallbackInfo ci) {
        Cynosure.LOGGER.info("Hello");
    }
}
