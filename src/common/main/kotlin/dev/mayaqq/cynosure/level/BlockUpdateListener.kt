package dev.mayaqq.cynosure.level

import dev.mayaqq.cynosure.injection.ILevel
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.msrandom.stub.Stub

public interface BlockUpdateListener {

    /**
     * The block positions this update listener listens to
     */
    public val listenedPositions: Iterable<BlockPos>

    /**
     * Called after each update before [onBlockUpdate], if this returns true the listener will be removed from the level
     */
    public fun shouldRemove(): Boolean

    /**
     * Handle block updates here. Note that the provided [BlockPos] and [BlockState] are of the
     * block update, not the listener itself
     * @param level [Level] in which the update happened
     * @param pos [BlockPos] of the block update
     * @param state [BlockState] at the updated position
     */
    public fun onBlockUpdate(level: Level, pos: BlockPos, state: BlockState)

}

public fun Level.addUpdateListener(listener: BlockUpdateListener) {
    (this as ILevel).cynosure_addUpdateListener(listener)
}