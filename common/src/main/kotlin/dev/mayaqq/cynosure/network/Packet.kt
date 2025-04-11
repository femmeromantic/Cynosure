package dev.mayaqq.cynosure.network

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MetaSerializable


@OptIn(ExperimentalSerializationApi::class)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MetaSerializable
public annotation class Packet(
    val id: String
) {
    /**
     * Optional interface providing a client-side handler
     */
    public interface Clientbound {
        public fun ClientNetworkingContext.handle()
    }

    /**
     * Optional interface providing a server-side handler
     */
    public interface Serverbound {
        public fun ServerNetworkingContext.handle()
    }
}

/**
 * Like [Packet] but doesn't mark the class as serializable
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
public annotation class CustomPacket(
    val id: String,
)

@Suppress("NOTHING_TO_INLINE")
internal inline fun ClientNetworkingContext.handlePacket(packet: Packet.Clientbound) {
    packet.run { this@handlePacket.handle() }
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun ServerNetworkingContext.handlePacket(packet: Packet.Serverbound) {
    packet.run { this@handlePacket.handle() }
}


