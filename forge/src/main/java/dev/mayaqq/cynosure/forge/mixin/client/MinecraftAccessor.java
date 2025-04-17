package dev.mayaqq.cynosure.forge.mixin.client;

import dev.mayaqq.cynosure.CynosureInternal;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@CynosureInternal
@Mixin(Minecraft.class)
public interface MinecraftAccessor {

    @Accessor("resourceManager")
    ReloadableResourceManager getResourceManager();
}
