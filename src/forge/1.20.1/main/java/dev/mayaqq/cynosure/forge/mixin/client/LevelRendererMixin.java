package dev.mayaqq.cynosure.forge.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mayaqq.cynosure.client.events.render.LevelRenderEvent;
import dev.mayaqq.cynosure.events.api.MainBus;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(
        method = "renderLevel",
        at = @At("HEAD")
    )
    public void onBeginWorldRender(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        var event = new LevelRenderEvent.Start((LevelRenderer) (Object) this, poseStack, partialTick, camera, null, null);
        MainBus.INSTANCE.post(event, null, null);
    }
}
