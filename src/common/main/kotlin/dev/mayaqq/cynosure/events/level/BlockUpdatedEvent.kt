package dev.mayaqq.cynosure.events.level

import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

public sealed class BlockEvent(
    public val level: Level,
    public val pos: BlockPos,
    public val state: BlockState
) : Event {

}