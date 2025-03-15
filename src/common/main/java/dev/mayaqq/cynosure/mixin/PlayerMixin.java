package dev.mayaqq.cynosure.mixin;

import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.entity.player.PlayerAttributeEvent;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "createAttributes", require = 1, allow = 1, at = @At("RETURN"))
    private static void onPlayerAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        MainBus.INSTANCE.post(new PlayerAttributeEvent(cir.getReturnValue()), null, null);
    }
}
