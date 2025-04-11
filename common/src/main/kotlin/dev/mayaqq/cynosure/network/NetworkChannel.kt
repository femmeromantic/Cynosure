package dev.mayaqq.cynosure.network

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.network.serialization.KByteCodec
import dev.mayaqq.cynosure.utils.set
import kotlinx.serialization.serializer
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

public inline fun NetworkChannel(channelId: ResourceLocation, protocolVersion: Int, builder: NetworkChannel.() -> Unit): NetworkChannel {
    val channel = NetworkChannel(channelId, protocolVersion)
    channel.builder()
    return channel
}

public class NetworkChannel(
    public val channelId: ResourceLocation,
    public val protocolVersion: Int
) {
    @OptIn(CynosureInternal::class)
    private val network: Network = NetworkProvider.createNetwork(channelId, protocolVersion)

    private val packetCodecs: MutableMap<KClass<*>, ByteCodec<*>> = mutableMapOf()
    private val packets: Table<KClass<*>, NetworkDirection, ResourceLocation> = HashBasedTable.create()

    public fun <T : Any> clientbound(klass: KClass<T>, codec: ByteCodec<T>, handler: ClientNetworkingContext.(T) -> Unit) {
        val id = klass.packetId ?: return
        packetCodecs[klass] = codec
        packets[klass, NetworkDirection.CLIENTBOUND] = id
        network.registerClientboundReceiver(klass, id, codec, handler)
    }


    public inline fun <reified T : Any> clientbound(codec: ByteCodec<T>? = null, noinline handler: ClientNetworkingContext.(T) -> Unit) {
        clientbound(T::class, codec ?: KByteCodec(serializer<T>()), handler)
    }

    public fun <T : Any> serverbound(klass: KClass<T>, codec: ByteCodec<T>, handler: ServerNetworkingContext.(T) -> Unit) {
        val id = klass.packetId ?: return
        packetCodecs[klass] = codec
        packets[klass, NetworkDirection.SERVERBOUND] = id
        network.registerServerboundReceiver(klass, id, codec, handler)
    }

    public inline fun <reified T : Any> serverbound(codec: ByteCodec<T>? = null, noinline handler: ServerNetworkingContext.(T) -> Unit) {
        serverbound(T::class, codec ?: KByteCodec(serializer<T>()), handler)
    }

    public fun <T : Packet.Clientbound> clientbound(klass: KClass<T>, codec: ByteCodec<T>) {
        clientbound(klass, codec, ClientNetworkingContext::handlePacket)
    }

    public inline fun <reified T : Packet.Clientbound> clientbound(codec: ByteCodec<T>? = null) {
        this.clientbound(T::class, codec ?: KByteCodec(serializer<T>()))
    }

    public fun <T : Packet.Serverbound> serverbound(klass: KClass<T>, codec: ByteCodec<T>) {
        serverbound(klass, codec, ServerNetworkingContext::handlePacket)
    }

    public inline fun <reified T : Packet.Serverbound> serverbound(codec: ByteCodec<T>? = null) {
        serverbound(T::class, codec ?: KByteCodec(serializer<T>()))
    }

    public fun <T : Any> sendToClient(client: ServerPlayer, packet: T) {
        val klass = packet::class
        val id = packets[klass, NetworkDirection.CLIENTBOUND] ?: error("Packet not registered $packet")
        val codec = packetCodecs[klass] as ByteCodec<T>
        network.sendToClient(client, id, codec, packet)
    }

    public fun <T : Any> sendToServer(packet: T) {
        val klass = packet::class
        val id = packets[klass, NetworkDirection.SERVERBOUND] ?: error("Packet not registered $packet")
        val codec = packetCodecs[klass] as ByteCodec<T>
        network.sendToServer(id, codec, packet)
    }

    public fun canSendToPlayer(player: ServerPlayer): Boolean = network.canSendToPlayer(player)

    private val KClass<*>.packetId: ResourceLocation?
        get() = (findAnnotation<CustomPacket>()?.id ?: findAnnotation<Packet>()?.id)
            ?.let { ResourceLocation(channelId.namespace, "${channelId.path}/$it") }
}