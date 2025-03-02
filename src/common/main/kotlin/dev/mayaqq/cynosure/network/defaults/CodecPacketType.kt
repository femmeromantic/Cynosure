package dev.mayaqq.cynosure.network.defaults

import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.network.Packet
import dev.mayaqq.cynosure.network.base.PacketType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation

public interface CodecPacketType<T : Packet<T>> : PacketType<T> {
    public fun codec(): ByteCodec<T>

    override fun encode(packet: T, buffer: FriendlyByteBuf) {
        codec().encode(packet, buffer)
    }

    override fun decode(buffer: FriendlyByteBuf): T = codec().decode(buffer)

    public abstract class Client<T : Packet<T>>(type: Class<T>, id: ResourceLocation, public val codec: ByteCodec<T>) : AbstractPacketType.Client<T>(type, id), CodecPacketType<T> {
        override fun codec(): ByteCodec<T> = codec
    }

    public abstract class Server<T : Packet<T>>(type: Class<T>, id: ResourceLocation, public val codec: ByteCodec<T>) : AbstractPacketType.Server<T>(type, id), CodecPacketType<T> {
        override fun codec(): ByteCodec<T> = codec
    }
}