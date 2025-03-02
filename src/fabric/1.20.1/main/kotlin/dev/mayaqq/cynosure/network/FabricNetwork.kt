package dev.mayaqq.cynosure.network

import dev.mayaqq.cynosure.network.base.ClientBoundPacketType
import dev.mayaqq.cynosure.network.base.DummyNetwork
import dev.mayaqq.cynosure.network.base.Network
import dev.mayaqq.cynosure.network.base.ServerBoundPacketType
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

public class FabricNetwork(modid: String, protocolVersion: Int, channel: String) : Network {

    public val client: Network = if (FabricLoader.getInstance().environmentType.equals(EnvType.CLIENT)) {
        FabricClientNetwork(ResourceLocation(modid, "$channel/v$protocolVersion"))
    } else {
        DummyNetwork.INSTANCE
    }

    public val server: Network = FabricServerNetwork(ResourceLocation(modid, "$channel/v$protocolVersion"))

    override fun <T : Packet<T>> register(type: ClientBoundPacketType<T>) {
        client.register(type)
    }

    override fun <T : Packet<T>> register(type: ServerBoundPacketType<T>) {
        server.register(type)
    }

    override fun <T : Packet<T>> sendToServer(packet: T) {
        client.sendToServer(packet)
    }

    override fun <T : Packet<T>> sendToClient(packet: T, player: ServerPlayer) {
        server.sendToClient(packet, player)
    }

    override fun canSendToPlayer(player: ServerPlayer): Boolean {
        return server.canSendToPlayer(player)
    }
}