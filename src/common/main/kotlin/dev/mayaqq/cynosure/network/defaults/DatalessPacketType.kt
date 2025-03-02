package dev.mayaqq.cynosure.network.defaults

import dev.mayaqq.cynosure.network.Packet
import dev.mayaqq.cynosure.network.base.PacketType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import java.util.function.Supplier

public interface DatalessPacketType<T : Packet<T>> : PacketType<T> {
    public fun create(): T

    override fun encode(packet: T, buffer: FriendlyByteBuf) {
        // Do nothing :3
    }

    override fun decode(buffer: FriendlyByteBuf): T = create()

    public abstract class Client<T : Packet<T>>(type: Class<T>, id: ResourceLocation, public val factory: Supplier<T>) : AbstractPacketType.Client<T>(type, id), DatalessPacketType<T> {
        override fun create(): T = factory.get()
    }

    public abstract class Server<T : Packet<T>>(type: Class<T>, id: ResourceLocation, public val factory: Supplier<T>) : AbstractPacketType.Server<T>(type, id), DatalessPacketType<T> {
        override fun create(): T = factory.get()
    }
}