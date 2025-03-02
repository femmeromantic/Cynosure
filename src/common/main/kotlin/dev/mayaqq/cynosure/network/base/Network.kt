package dev.mayaqq.cynosure.network.base

import dev.mayaqq.cynosure.network.Packet
import net.minecraft.server.level.ServerPlayer

public interface Network {
    public fun <T : Packet<T>> register(type: ClientBoundPacketType<T>)

    public fun <T : Packet<T>> register(type: ServerBoundPacketType<T>)

    public fun <T : Packet<T>> sendToServer(packet: T)

    public fun <T : Packet<T>> sendToClient(packet: T, player: ServerPlayer)

    public fun canSendToPlayer(player: ServerPlayer): Boolean
}