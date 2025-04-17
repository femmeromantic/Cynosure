package dev.mayaqq.cynosure.blocks

import dev.mayaqq.cynosure.extensions.Extension
import dev.mayaqq.cynosure.extensions.ExtensionRegistry
import net.minecraft.world.level.block.Block

public interface BlockExtension : Extension<Block> {

    public companion object Registry : ExtensionRegistry<Block, BlockExtension>(
        Block::class.java, BlockExtension::class.java
    )
}