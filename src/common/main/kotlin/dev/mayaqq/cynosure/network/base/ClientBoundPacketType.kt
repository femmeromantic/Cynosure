package dev.mayaqq.cynosure.network.base

import dev.mayaqq.cynosure.network.Packet

public interface ClientBoundPacketType<T : Packet<T>> : PacketType<T> {
    public fun T.handle()
}