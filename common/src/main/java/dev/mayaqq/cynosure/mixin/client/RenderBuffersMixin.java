package dev.mayaqq.cynosure.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.BufferBuilder;
import dev.mayaqq.cynosure.client.render.RenderTypeRegistry;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.SortedMap;

@Mixin(RenderBuffers.class)
public class RenderBuffersMixin {
    @Shadow
    @Final
    @Mutable
    private SortedMap<RenderType, BufferBuilder> fixedBuffers;

    @WrapOperation(
        method = "<init>",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBuffers;fixedBuffers:Ljava/util/SortedMap;",
            opcode = Opcodes.PUTFIELD
        ),
        remap = false
    )
    @SuppressWarnings("KotlinInternalInJava")
    public void addCustomBuffers(RenderBuffers instance, SortedMap<RenderType, BufferBuilder> value, Operation<Void> original) {
        value.putAll(RenderTypeRegistry.TYPES);
        RenderTypeRegistry.frozen = true;
        original.call(instance, value);
    }
}
