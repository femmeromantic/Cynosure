package dev.mayaqq.cynosure.network

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MetaSerializable


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
public annotation class Packet(
    val id: String
) {
    /**
     * Optional interface providing a client-side handler
     */
    public interface Clientbound {
        public fun ClientNetworkContext.handle()
    }

    /**
     * Optional interface providing a server-side handler
     */
    public interface Serverbound {
        public fun ServerNetworkContext.handle()
    }
}

/**
 * Like [Packet] but also marks the class as serializable
 */
@OptIn(ExperimentalSerializationApi::class)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MetaSerializable
public annotation class SerializablePacket(
    val id: String,
)

@Suppress("NOTHING_TO_INLINE")
internal inline fun ClientNetworkContext.handlePacket(packet: Packet.Clientbound) {
    packet.run { this@handlePacket.handle() }
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun ServerNetworkContext.handlePacket(packet: Packet.Serverbound) {
    packet.run { this@handlePacket.handle() }
}


