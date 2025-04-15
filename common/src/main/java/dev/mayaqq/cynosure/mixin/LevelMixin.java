package dev.mayaqq.cynosure.mixin;

import dev.mayaqq.cynosure.injection.ILevel;
import dev.mayaqq.cynosure.level.BlockUpdateListener;
import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import java.util.Iterator;
import java.util.Set;

@Mixin(Level.class)
public abstract class LevelMixin implements ILevel {

    @Unique
    private Long2ReferenceMap<Set<BlockUpdateListener>> cynosure$updateListeners;

    @Override
    public void cynosure_addUpdateListener(BlockUpdateListener listener) {
        if(cynosure$updateListeners == null) cynosure$updateListeners = new Long2ReferenceOpenHashMap<>();
        for(BlockPos pos : listener.getListenedPositions()) {
            long longPos = pos.asLong();
            var set = cynosure$updateListeners.get(longPos);
            if (set == null) {
                set = new ObjectArraySet<>();
                cynosure$updateListeners.put(longPos, set);
            }
            set.add(listener);
        }
    }

    @Override
    public void cynosure_handleBlockUpdate(BlockPos pos, BlockState state) {
        if(cynosure$updateListeners == null) return;
        long longPos = pos.asLong();
        Set<BlockUpdateListener> listeners = cynosure$updateListeners.get(longPos);
        if(listeners != null) {
            Iterator<BlockUpdateListener> iterator = listeners.iterator();
            while (iterator.hasNext()) {
                BlockUpdateListener listener = iterator.next();
                if(listener.shouldRemove()) {
                    iterator.remove();
                } else {
                    listener.onBlockUpdate((Level) (Object) this, pos, state);
                }
            }
            if(listeners.isEmpty()) cynosure$updateListeners.remove(longPos);
        }
    }
}
