package dev.mayaqq.cynosure.network.base

import dev.mayaqq.cynosure.network.Packet
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.player.Player

public interface ServerBoundPacketType<T : Packet<T>> : PacketType<T> {
    public fun T.handle(server: MinecraftServer, player: Player)
}