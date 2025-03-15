package dev.mayaqq.cynosure.events.entity.player

import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.world.entity.player.Player

public sealed class PlayerTickEvents(public val player: Player) : Event {

    public class End(player: Player) : PlayerTickEvents(player)

    public class Begin(player: Player) : PlayerTickEvents(player)
}

public sealed class PlayerConnectionEvents(public val player: Player) : Event {

    public class Join(player: Player) : PlayerConnectionEvents(player)

    public class Leave(player: Player) : PlayerConnectionEvents(player)
}