package dev.mayaqq.cynosure.network.base

import dev.mayaqq.cynosure.network.Packet

public interface ServerBoundPacketType<T : Packet<T>> : PacketType<T> {
    public fun handle(packet: T)
}