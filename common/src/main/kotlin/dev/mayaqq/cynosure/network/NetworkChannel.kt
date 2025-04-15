package dev.mayaqq.cynosure.network

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.network.serialization.KByteCodec
import dev.mayaqq.cynosure.utils.set
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializerOrNull
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.typeOf

/**
 * Create a new network channel, an alternative to the constructor providing a builder to register packets
 */
public inline fun NetworkChannel(channelId: ResourceLocation, protocolVersion: Int, builder: NetworkChannel.() -> Unit): NetworkChannel {
    val channel = NetworkChannel(channelId, protocolVersion)
    channel.builder()
    return channel
}

public class NetworkChannel(
    public val channelId: ResourceLocation,
    protocolVersion: Int
) {
    @OptIn(CynosureInternal::class)
    private val network: Network = NetworkProvider.createNetwork(channelId, protocolVersion)

    private val packetCodecs: MutableMap<KClass<*>, ByteCodec<*>> = mutableMapOf()
    private val packets: Table<KClass<*>, NetworkDirection, ResourceLocation> = HashBasedTable.create()

    public fun <T : Any> clientbound(klass: KClass<T>, codec: ByteCodec<T>, handler: ClientNetworkContext.(T) -> Unit) {
        val id = klass.packetId ?: return
        packetCodecs[klass] = codec
        packets[klass, NetworkDirection.CLIENTBOUND] = id
        network.registerClientboundReceiver(klass, id, codec, handler)
    }

    public inline fun <reified T : Any> clientbound(codec: ByteCodec<T>? = null, noinline handler: ClientNetworkContext.(T) -> Unit) {
        clientbound(T::class, codec ?: getByteCodecOrThrow(), handler)
    }

    public fun <T : Packet.Clientbound> clientbound(klass: KClass<T>, codec: ByteCodec<T>) {
        clientbound(klass, codec, ClientNetworkContext::handlePacket)
    }

    public inline fun <reified T : Packet.Clientbound> clientbound(codec: ByteCodec<T>? = null) {
        clientbound(T::class, codec ?: getByteCodecOrThrow<T>())
    }

    public fun <T : Any> serverbound(klass: KClass<T>, codec: ByteCodec<T>, handler: ServerNetworkContext.(T) -> Unit) {
        val id = klass.packetId ?: return
        packetCodecs[klass] = codec
        packets[klass, NetworkDirection.SERVERBOUND] = id
        network.registerServerboundReceiver(klass, id, codec, handler)
    }

    public inline fun <reified T : Any> serverbound(codec: ByteCodec<T>? = null, noinline handler: ServerNetworkContext.(T) -> Unit) {
        serverbound(T::class, codec ?: getByteCodecOrThrow(), handler)
    }

    public fun <T : Packet.Serverbound> serverbound(klass: KClass<T>, codec: ByteCodec<T>) {
        serverbound(klass, codec, ServerNetworkContext::handlePacket)
    }

    public inline fun <reified T : Packet.Serverbound> serverbound(codec: ByteCodec<T>? = null) {
        serverbound(T::class, codec ?: getByteCodecOrThrow<T>())
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
        get() = (findAnnotation<SerializablePacket>()?.id ?: findAnnotation<Packet>()?.id)
            ?.let { ResourceLocation(channelId.namespace, "${channelId.path}/$it") }


}

@PublishedApi
internal inline fun <reified T> getByteCodecOrThrow(): ByteCodec<T> =
    T::class.objectInstance?.let { ByteCodec.unit(it) }
        ?: serializerOrNull(typeOf<T>())?.cast<T>()?.let(::KByteCodec)
        ?: error("You have to pass a custom bytecodec for non-serializable packets with data: ${T::class}")

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@PublishedApi
internal inline fun <T> KSerializer<*>.cast(): KSerializer<T> = this as KSerializer<T>

@Suppress("NOTHING_TO_INLINE")
internal inline fun ClientNetworkContext.handlePacket(packet: Packet.Clientbound) {
    packet.run { this@handlePacket.handle() }
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun ServerNetworkContext.handlePacket(packet: Packet.Serverbound) {
    packet.run { this@handlePacket.handle() }
}