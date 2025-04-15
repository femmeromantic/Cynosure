package dev.mayaqq.cynosure.fabric.mixin;

import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.block.FallingBlockEvent;
import net.minecraft.world.entity.item.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {

//    @Inject(
//        method = "tick",
//        at = @At("HEAD")
//    )
//    private void tickFallingBlockEntity(CallbackInfo ci) {
//
//        if (!MainBus.INSTANCE.post(FallingBlockEvent.Tick()))
//    }
}
