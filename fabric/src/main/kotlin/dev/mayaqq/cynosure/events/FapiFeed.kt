package dev.mayaqq.cynosure.events

import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.events.data.DataPackSyncEvent
import dev.mayaqq.cynosure.events.entity.LivingEntityEvent
import dev.mayaqq.cynosure.events.entity.player.PlayerConnectionEvent
import dev.mayaqq.cynosure.events.world.LevelEvent
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents

internal object FapiFeed {
    fun feed() {
        ServerPlayConnectionEvents.JOIN.register { handler, _, _ -> PlayerConnectionEvent.Join(handler.player).post() }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, _ -> PlayerConnectionEvent.Leave(handler.player).post() }
        ServerLivingEntityEvents.AFTER_DEATH.register { entity, source -> LivingEntityEvent.Death(entity, source).post() }
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register { player, isJoin -> DataPackSyncEvent(player, isJoin).post() }
        ServerWorldEvents.LOAD.register { _, level -> LevelEvent.Load(level).post(context = "server") }
        ServerWorldEvents.UNLOAD.register { _, level -> LevelEvent.Unload(level).post(context = "server") }
        ServerTickEvents.START_WORLD_TICK.register { LevelEvent.StartTick(it).post(context = "server") }
        ServerTickEvents.END_WORLD_TICK.register { LevelEvent.EndTick(it).post(context = "server") }
    }
}