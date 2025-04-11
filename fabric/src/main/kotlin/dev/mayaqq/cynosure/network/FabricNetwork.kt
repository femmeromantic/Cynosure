package dev.mayaqq.cynosure.network

import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.CynosureInternal
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import kotlin.reflect.KClass

internal class FabricNetwork(
    private val channel: ResourceLocation,
    private val protocolVersion: Int
) : Network {

    val client: FabricClientNetwork? =
        if(FabricLoader.getInstance().environmentType == EnvType.CLIENT) FabricClientNetwork else null

    private fun ResourceLocation.withProtocolVersion() = ResourceLocation(namespace, "$path/v$protocolVersion")

    override fun <T : Any> sendToClient(
        client: ServerPlayer,
        id: ResourceLocation,
        serializer: ByteCodec<T>,
        packet: T
    ) {
        FabricServerNetwork.send(client, id.withProtocolVersion(), serializer, packet)
    }

    override fun <T : Any> sendToServer(id: ResourceLocation, serializer: ByteCodec<T>, packet: T) {
        client?.send(id.withProtocolVersion(), serializer, packet)
    }

    override fun <T : Any> registerClientboundReceiver(
        type: KClass<T>,
        id: ResourceLocation,
        codec: ByteCodec<T>,
        handler: ClientNetworkingContext.(T) -> Unit
    ) {
        client?.register(id.withProtocolVersion(), codec, handler)
    }

    override fun <T : Any> registerServerboundReceiver(
        type: KClass<T>,
        id: ResourceLocation,
        codec: ByteCodec<T>,
        handler: ServerNetworkingContext.(T) -> Unit
    ) {
        FabricServerNetwork.register(id.withProtocolVersion(), codec, handler)
    }

    override fun canSendToPlayer(player: ServerPlayer): Boolean {
        ServerPlayNetworking.getSendable(player.connection).forEach { location ->
            if (location.namespace.equals(channel.namespace) && location.path.startsWith(channel.path)) {
                return true
            }
        }
        return false
    }
}

@OptIn(CynosureInternal::class)
public class NetworkProviderImpl : NetworkProvider {
    override fun createNetwork(networkId: ResourceLocation, protocolVersion: Int): Network {
        return FabricNetwork(networkId, protocolVersion)
    }
}