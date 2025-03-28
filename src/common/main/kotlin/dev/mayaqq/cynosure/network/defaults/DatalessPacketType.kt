package dev.mayaqq.cynosure.network.defaults

import dev.mayaqq.cynosure.network.Packet
import dev.mayaqq.cynosure.network.base.ClientBoundPacketType
import dev.mayaqq.cynosure.network.base.PacketType
import dev.mayaqq.cynosure.network.base.ServerBoundPacketType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation

public interface DatalessPacketType<T : Packet<T>> : PacketType<T> {
    public val instance: T

    override fun encode(packet: T, buffer: FriendlyByteBuf) {
        // Do nothing :3
    }

    override fun decode(buffer: FriendlyByteBuf): T = instance

    public abstract class Client<T : Packet<T>>(override val klass: Class<T>, override val id: ResourceLocation, override val instance: T) : ClientBoundPacketType<T>, DatalessPacketType<T>

    public abstract class Server<T : Packet<T>>(override val klass: Class<T>, override val id: ResourceLocation, override val instance: T) : ServerBoundPacketType<T>, DatalessPacketType<T>

}

public sealed interface DatalessPacket<T : Packet<T>> : Packet<T>, DatalessPacketType<T> {

    override val type: PacketType<T>
        get() = this

    public abstract class Client<Self : Packet<Self>>(
        override val id: ResourceLocation
    ) : ClientBoundPacketType<Self>, DatalessPacket<Self> {
        override val klass: Class<Self> = javaClass as Class<Self>
        override val instance: Self = this as Self
    }

    public abstract class Server<Self : Packet<Self>>(
        override val id: ResourceLocation
    ) : ServerBoundPacketType<Self>, DatalessPacket<Self> {
        override val klass: Class<Self> = javaClass as Class<Self>
        override val instance: Self = this as Self
    }

}