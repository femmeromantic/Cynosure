@file:Mod.EventBusSubscriber(modid = MODID)
package dev.mayaqq.cynosure.events

import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.MODID
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.events.data.DataPackSyncEvent
import dev.mayaqq.cynosure.events.entity.LivingEntityEvent
import dev.mayaqq.cynosure.events.entity.MountEvent
import dev.mayaqq.cynosure.events.entity.player.PlayerConnectionEvent
import dev.mayaqq.cynosure.internal.CynosureHooksImpl
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.OnDatapackSyncEvent
import net.minecraftforge.event.TickEvent.LevelTickEvent
import net.minecraftforge.event.TickEvent.Phase
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.event.entity.EntityLeaveLevelEvent
import net.minecraftforge.event.entity.EntityMountEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.level.LevelEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLEnvironment

private val distContext = if(FMLEnvironment.dist.isClient) "client" else "server"

@SubscribeEvent
public fun onPlayerJoin(event: EntityJoinLevelEvent) {
    if (event.entity is ServerPlayer) PlayerConnectionEvent.Join(event.entity as ServerPlayer).post()
}

@SubscribeEvent
public fun onPlayerLeave(event: EntityLeaveLevelEvent) {
    if (event.entity is ServerPlayer) PlayerConnectionEvent.Leave(event.entity as ServerPlayer).post()
}

@SubscribeEvent
public fun onEntityDeath(event: LivingDeathEvent) {
    LivingEntityEvent.Death(event.entity, event.source).post()
}

@SubscribeEvent
public fun onSyncDatapack(event: OnDatapackSyncEvent) {
    if (event.player != null) {
        DataPackSyncEvent(event.player!!, true).post()
    } else {
        for (player in event.playerList.players) {
            DataPackSyncEvent(player, false).post()
        }
    }
}

@SubscribeEvent
public fun onEntityMount(event: EntityMountEvent) {
    MountEvent(event.entityMounting, event.entityBeingMounted, event.isMounting).post()
}

@SubscribeEvent
public fun onLoadWorld(event: LevelEvent.Load) {
    dev.mayaqq.cynosure.events.world.LevelEvent.Load(event.level as Level)
        .post(context = distContext)
}

@SubscribeEvent
public fun onUnloadWorld(event: LevelEvent.Unload) {
    dev.mayaqq.cynosure.events.world.LevelEvent.Unload(event.level as Level)
        .post(context = distContext)
}

@SubscribeEvent
public fun onSaveWorld(event: LevelEvent.Save) {
    dev.mayaqq.cynosure.events.world.LevelEvent.Save(event.level as ServerLevel).post()
}

@SubscribeEvent
public fun onTickWorld(event: LevelTickEvent) {
    val e = when(event.phase!!) {
        Phase.START -> dev.mayaqq.cynosure.events.world.LevelEvent.StartTick(event.level)
        Phase.END -> dev.mayaqq.cynosure.events.world.LevelEvent.EndTick(event.level)
    }
    e.post(context = distContext)
}

@OptIn(CynosureInternal::class)
@SubscribeEvent
public fun onReloadListener(event: AddReloadListenerEvent) {
    CynosureHooksImpl.SERVER_RELOAD_LISTENERS.forEach { (_, listener) -> event.addListener(listener) }
}