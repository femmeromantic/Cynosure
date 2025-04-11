package dev.mayaqq.cynosure.client.blockEntity

import net.minecraft.core.BlockPos
import net.minecraft.network.Connection
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

public class SyncedBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState
) : BlockEntity(type, pos, state) {


}

public interface CustomDataPacketReceivingBlockEntity {
    public fun onDataSyncPacket(connection: Connection, packet: ClientboundBlockEntityDataPacket)
}

public interface CustomTagUpdateHandlingBlockEntity {

}