package dev.mayaqq.cynosure.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.mayaqq.cynosure.injection.ILevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChunkHolder.class)
@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
public class ChunkHolderMixin {

    @Inject(
        method = "lambda$broadcastChanges$0",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ChunkHolder;broadcastBlockEntityIfNeeded(Ljava/util/List;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V")
    )
    public void handleUpdate(List list, Level level, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        if(!level.isClientSide) ((ILevel) level).cynosure_handleBlockUpdate(blockPos, blockState);
    }

    @Inject(
        method = "broadcastChanges",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ChunkHolder;broadcastBlockEntityIfNeeded(Ljava/util/List;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V")
    )
    public void handleSingleUpdate(LevelChunk chunk, CallbackInfo ci, @Local Level level, @Local BlockPos pos, @Local BlockState state) {
        if(!level.isClientSide) ((ILevel) level).cynosure_handleBlockUpdate(pos, state);
    }
}
