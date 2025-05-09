package dev.mayaqq.cynosure.mixin.accessor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ChunkMap.class)
public interface ChunkMapAccessor {
    @Accessor
    Int2ObjectMap<TrackedEntityAccessor> getEntityMap();
}
