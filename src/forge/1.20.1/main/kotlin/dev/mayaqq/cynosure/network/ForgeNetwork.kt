package dev.mayaqq.cynosure.network

import dev.mayaqq.cynosure.network.base.ClientBoundPacketType
import dev.mayaqq.cynosure.network.base.Network
import dev.mayaqq.cynosure.network.base.ServerBoundPacketType
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import java.util.function.BooleanSupplier

public class ForgeNetwork(
    name: ResourceLocation,
    protocolVersion: Int,
    optional: BooleanSupplier,
    private val channel: SimpleChannel = NetworkRegistry.newSimpleChannel(
        name,
        { protocolVersion.toString() },
        { it == protocolVersion.toString() || optional.asBoolean },
        { it == protocolVersion.toString() || optional.asBoolean }
    )
) : Network {

    private var packets = 0

    override fun <T : Packet<T>> register(type: ClientBoundPacketType<T>) {
        channel.registerMessage(
            ++packets,
            type.type(),
            type::encode,
            type::decode,
        ) { packet, ctx ->
            val context = ctx.get()
            context.enqueueWork { type.handle(packet) }
            context.packetHandled = true
        }
    }

    override fun <T : Packet<T>> register(type: ServerBoundPacketType<T>) {
        channel.registerMessage(
            ++packets,
            type.type(),
            type::encode,
            type::decode,
        ) { packet, ctx ->
            val context = ctx.get()
            context.enqueueWork { type.handle(packet) }
            context.packetHandled = true
        }
    }

    override fun <T : Packet<T>> sendToServer(packet: T) {
        channel.sendToServer(packet)
    }

    override fun <T : Packet<T>> sendToClient(packet: T, player: ServerPlayer) {
        channel.send(PacketDistributor.PLAYER.with{ player }, packet)
    }

    override fun canSendToPlayer(player: ServerPlayer): Boolean {
        return channel.isRemotePresent(player.connection.connection)
    }
}
