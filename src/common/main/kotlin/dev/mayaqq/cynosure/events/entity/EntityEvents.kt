package dev.mayaqq.cynosure.events.entity

import dev.mayaqq.cynosure.events.InteractionResultEvent
import dev.mayaqq.cynosure.events.api.CancellableEvent
import dev.mayaqq.cynosure.events.api.Event
import dev.mayaqq.cynosure.events.api.ReturningEvent
import dev.mayaqq.cynosure.events.api.RootEventClass
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

public class EntityInteractionEvent(
    public val level: Level,
    public val player: Player,
    public val entity: Entity,
    public val hand: InteractionHand
): InteractionResultEvent() {
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

@RootEventClass
public sealed class LivingEntityEvent(public val entity: LivingEntity) : CancellableEvent() {
    public class Death(entity: LivingEntity, public val source: DamageSource) : LivingEntityEvent(entity)
}

public class MountEvent(public val entity: Entity, public val mount: Entity?, public val isMounting: Boolean) : Event

public class EntityDamageSourceEvent(public val entity: Entity, public val source: DamageSource) : ReturningEvent<DamageSource>()

public class EntityDamageEvent(public val entity: Entity, public val source: DamageSource, public val amount: Float) : ReturningEvent<Float>()