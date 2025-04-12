package dev.mayaqq.cynosure.network

import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.CynosureInternal
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.network.NetworkEvent.Context
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import kotlin.reflect.KClass


internal class ForgeNetwork(
    name: ResourceLocation,
    protocolVersion: Int
) : Network {

    private val channel: SimpleChannel = NetworkRegistry.newSimpleChannel(
        name,
        { protocolVersion.toString() },
        { it == protocolVersion.toString() },
        { it == protocolVersion.toString() }
    )

    private var packets: Int = 0

    override fun <T : Any> registerClientboundReceiver(
        type: KClass<T>,
        id: ResourceLocation,
        codec: ByteCodec<T>,
        handler: ClientNetworkContext.(T) -> Unit
    ) {
        @Suppress("INACCESSIBLE_TYPE")
        channel.registerMessage(
            packets++,
            type.java,
            codec::encode,
            codec::decode
        ) { packet, contextSupplier ->
            val ctx = contextSupplier.get()
            ClientContext(ctx).handler(packet)
            ctx.packetHandled = true
        }
    }

    override fun <T : Any> registerServerboundReceiver(
        type: KClass<T>,
        id: ResourceLocation,
        codec: ByteCodec<T>,
        handler: ServerNetworkContext.(T) -> Unit
    ) {
        @Suppress("INACCESSIBLE_TYPE")
        channel.registerMessage(
            packets++,
            type.java,
            codec::encode,
            codec::decode
        ) { packet, contextSupplier ->
            val ctx = contextSupplier.get()
            ServerContext(ctx).handler(packet)
            ctx.packetHandled = true
        }
    }

    override fun <T : Any> sendToClient(
        client: ServerPlayer,
        id: ResourceLocation,
        serializer: ByteCodec<T>,
        packet: T
    ) {
        channel.send(PacketDistributor.PLAYER.with { client }, packet)
    }

    override fun <T : Any> sendToServer(id: ResourceLocation, serializer: ByteCodec<T>, packet: T) {
        channel.sendToServer(packet)
    }

    override fun canSendToPlayer(player: ServerPlayer): Boolean {
        TODO("Not yet implemented")
    }


    private class ClientContext(val ctx: Context) : ClientNetworkContext() {
        override fun execute(action: () -> Unit) {
            ctx.enqueueWork(action)
        }
    }

    private class ServerContext(val ctx: Context) : ServerNetworkContext(
        ctx.sender!!.server, ctx.sender!!
    ) {
        override fun execute(action: () -> Unit) {
            ctx.enqueueWork(action)
        }
    }

}

@OptIn(CynosureInternal::class)
public class NetworkProviderImpl : NetworkProvider {
    override fun createNetwork(networkId: ResourceLocation, protocolVersion: Int): Network {
        return ForgeNetwork(networkId, protocolVersion)
    }
}

