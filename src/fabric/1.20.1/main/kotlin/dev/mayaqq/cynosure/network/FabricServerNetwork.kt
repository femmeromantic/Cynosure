package dev.mayaqq.cynosure.network

import dev.mayaqq.cynosure.network.base.ClientBoundPacketType
import dev.mayaqq.cynosure.network.base.Network
import dev.mayaqq.cynosure.network.base.ServerBoundPacketType
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

public class FabricServerNetwork(private val channel: ResourceLocation) : Network {
    override fun <T : Packet<T>> register(type: ClientBoundPacketType<T>) {
        throw IllegalStateException("Cannot register client bound packet types on server network")
    }

    override fun <T : Packet<T>> register(type: ServerBoundPacketType<T>) {
        ServerPlayNetworking.registerGlobalReceiver(createChannelLocation(channel, type.id)) { server, player, handler, buf, responseSender ->
            val decode = type.decode(buf)
            server.execute { type.handle(player, decode) }
        }
    }

    override fun <T : Packet<T>> sendToServer(packet: T) {
        throw IllegalStateException("Cannot send packets to server on server network")
    }

    override fun <T : Packet<T>> sendToClient(packet: T, player: ServerPlayer) {
        val type = packet.type
        val buf = FriendlyByteBuf(Unpooled.buffer())
        type.encode(packet, buf)
        ServerPlayNetworking.send(player, createChannelLocation(channel, type.id), buf)
    }

    override fun canSendToPlayer(player: ServerPlayer): Boolean {
        ServerPlayNetworking.getSendable(player.connection).forEach { location ->
            if (location.namespace.equals(channel.namespace) && location.path.startsWith(channel.path)) {
                return true
            }
        }
        return false
    }

    private companion object {
        fun createChannelLocation(channel: ResourceLocation, id: ResourceLocation) = ResourceLocation(channel.namespace, "${channel.path}/${id.namespace}/${id.path}")
    }
}