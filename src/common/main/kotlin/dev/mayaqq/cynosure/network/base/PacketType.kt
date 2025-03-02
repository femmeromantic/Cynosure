package dev.mayaqq.cynosure.network.base

import dev.mayaqq.cynosure.network.Packet
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation

public interface PacketType<T : Packet<T>> {
    public fun type(): Class<T>

    public fun id(): ResourceLocation

    public fun encode(packet: T, buffer: FriendlyByteBuf)

    public fun decode(buffer: FriendlyByteBuf): T
}