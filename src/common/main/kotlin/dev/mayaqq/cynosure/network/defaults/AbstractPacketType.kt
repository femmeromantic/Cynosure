package dev.mayaqq.cynosure.network.defaults

import dev.mayaqq.cynosure.network.Packet
import dev.mayaqq.cynosure.network.base.ClientBoundPacketType
import dev.mayaqq.cynosure.network.base.PacketType
import dev.mayaqq.cynosure.network.base.ServerBoundPacketType
import net.minecraft.resources.ResourceLocation

public abstract class AbstractPacketType<T : Packet<T>>(public val type: Class<T>, public val id: ResourceLocation) : PacketType<T> {
    override fun id(): ResourceLocation = id
    override fun type(): Class<T> = type

    public abstract class Client<T : Packet<T>>(type: Class<T>, id: ResourceLocation) : AbstractPacketType<T>(type, id), ClientBoundPacketType<T>
    public abstract class Server<T : Packet<T>>(type: Class<T>, id: ResourceLocation) : AbstractPacketType<T>(type, id), ServerBoundPacketType<T>
}