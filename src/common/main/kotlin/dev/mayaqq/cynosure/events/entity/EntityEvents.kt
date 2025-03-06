package dev.mayaqq.cynosure.events.entity

import dev.mayaqq.cynosure.events.api.Event
import dev.mayaqq.cynosure.events.api.ReturningEvent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

public class EntityInteractionEvent(
    public val level: Level,
    public val player: Player,
    public val entity: Entity,
    public val hand: InteractionHand
): ReturningEvent<InteractionResult>() {
    override val isCancelled: Boolean
        get() = super.isCancelled && result?.consumesAction() == true

    /**
     * Shorthand for getting the item stack in the active hand of the player
     */
    public fun getUsedStack(): ItemStack = player.getItemInHand(hand)
}

public sealed class EntityTrackingEvent(
    public val entity: Entity,
    public val player: ServerPlayer
) : Event {
    /**
     * Invoked when an entity starts tracking the player
     */
    public class Start(entity: Entity, player: ServerPlayer) : EntityTrackingEvent(entity, player)

    /**
     * Invoked when an entity stops tracking the player
     */
    public class Stop(entity: Entity, player: ServerPlayer) : EntityTrackingEvent(entity, player)
}