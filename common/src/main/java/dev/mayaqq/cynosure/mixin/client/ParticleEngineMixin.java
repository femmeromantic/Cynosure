package dev.mayaqq.cynosure.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.mayaqq.cynosure.client.events.ParticleRenderTypeRegistrationEvent;
import dev.mayaqq.cynosure.events.api.MainBus;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

    @Shadow @Final @Mutable
    private static List<ParticleRenderType> RENDER_ORDER;

    @ModifyExpressionValue(
        method = "<init>",
        at = @At(value = "RETURN")
    )
    private void modifyArray() {
        List<ParticleRenderType> renderTypes = new ArrayList<>(RENDER_ORDER);
        ParticleRenderTypeRegistrationEvent event = new ParticleRenderTypeRegistrationEvent((ParticleEngine) (Object) this, renderTypes);
        MainBus.INSTANCE.post(event, null, null);
        RENDER_ORDER = renderTypes;
    }
}
