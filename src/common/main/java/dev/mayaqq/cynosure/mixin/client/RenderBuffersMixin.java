package dev.mayaqq.cynosure.mixin.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import dev.mayaqq.cynosure.client.render.RenderTypeRegistry;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBuffers.class)
public class RenderBuffersMixin {

    @Inject(
        method = "lambda$new$1(Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;)V",
        at = @At("RETURN")
    )
    @SuppressWarnings({"MixinAnnotationTarget"})
    public void addCustomBuffers(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map, CallbackInfo ci) {
        map.putAll(RenderTypeRegistry.TYPES);
        RenderTypeRegistry.frozen = true;
    }
}
