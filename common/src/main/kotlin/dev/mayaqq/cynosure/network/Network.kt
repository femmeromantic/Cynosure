package dev.mayaqq.cynosure.network

import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.internal.loadPlatform
import dev.mayaqq.cynosure.utils.loadService
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import kotlin.reflect.KClass

public interface Network {

    public fun <T : Any> sendToClient(client: ServerPlayer, id: ResourceLocation, serializer: ByteCodec<T>, packet: T)

    public fun <T : Any> sendToServer(id: ResourceLocation, serializer: ByteCodec<T>, packet: T)

    public fun <T : Any> registerClientboundReceiver(
        type: KClass<T>,
        id: ResourceLocation,
        codec: ByteCodec<T>,
        handler: ClientNetworkingContext.(T) -> Unit
    )

    public fun <T : Any> registerServerboundReceiver(
        type: KClass<T>,
        id: ResourceLocation,
        codec: ByteCodec<T>,
        handler: ServerNetworkingContext.(T) -> Unit
    )

    public fun canSendToPlayer(player: ServerPlayer): Boolean
}

@CynosureInternal
public interface NetworkProvider {

    public companion object : NetworkProvider by loadPlatform()

    public fun createNetwork(networkId: ResourceLocation, protocolVersion: Int): Network
}

public sealed class NetworkingContext(public val direction: NetworkDirection) {

    public abstract fun execute(action: () -> Unit)

}

public enum class NetworkDirection {
    CLIENTBOUND,
    SERVERBOUND
}

public abstract class ServerNetworkingContext(
    public val server: MinecraftServer,
    public val sender: ServerPlayer
) : NetworkingContext(NetworkDirection.SERVERBOUND)


public abstract class ClientNetworkingContext : NetworkingContext(NetworkDirection.CLIENTBOUND)

