package dev.mayaqq.cynosure.events

import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.events.data.DataPackSyncEvent
import dev.mayaqq.cynosure.events.entity.LivingEntityEvent
import dev.mayaqq.cynosure.events.entity.player.PlayerConnectionEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.server.packs.PackType

internal object FapiFeed {
    fun feed() {
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server -> PlayerConnectionEvents.Join(handler.player).post() }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, server -> PlayerConnectionEvents.Leave(handler.player).post() }
        ServerLivingEntityEvents.AFTER_DEATH.register { entity, source -> LivingEntityEvent.Death(entity, source).post() }
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register { player, isJoin -> DataPackSyncEvent(player, isJoin).post() }
    }
}