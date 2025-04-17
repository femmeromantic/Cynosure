package dev.mayaqq.cynosure.events.server

import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.player.Player

public sealed class ServerEvent(
    public val server: MinecraftServer
) : Event {

    public class Starting(server: MinecraftServer) : ServerEvent(server)

    public class Started(server: MinecraftServer) : ServerEvent(server)

    public class Stopping(server: MinecraftServer) : ServerEvent(server)

    public class Stopped(server: MinecraftServer) : ServerEvent(server)

    public class BeginTick(server: MinecraftServer) : ServerEvent(server)

    public class EndTick(server: MinecraftServer) : ServerEvent(server)
}

public class DataPackSyncEvent(public val player: Player, public val isJoin: Boolean) : Event