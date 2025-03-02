package dev.mayaqq.cynosure.network

import dev.mayaqq.cynosure.network.base.PacketType

public interface Packet<T : Packet<T>> {
    public fun type(): PacketType<T>
}