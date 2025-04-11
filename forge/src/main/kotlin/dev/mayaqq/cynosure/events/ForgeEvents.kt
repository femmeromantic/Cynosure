@file:Mod.EventBusSubscriber(modid = MODID)
package dev.mayaqq.cynosure.events

import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.MODID
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.events.data.DataPackSyncEvent
import dev.mayaqq.cynosure.events.entity.LivingEntityEvent
import dev.mayaqq.cynosure.events.entity.MountEvent
import dev.mayaqq.cynosure.events.entity.player.PlayerConnectionEvents
import dev.mayaqq.cynosure.internal.CynosureHooksImpl
import net.minecraft.world.entity.player.Player
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.OnDatapackSyncEvent
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.event.entity.EntityLeaveLevelEvent
import net.minecraftforge.event.entity.EntityMountEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod


@SubscribeEvent
public fun onPlayerJoin(event: EntityJoinLevelEvent) {
    if (event.entity is Player) PlayerConnectionEvents.Join(event.entity as Player).post()
}

@SubscribeEvent
public fun onPlayerLeave(event: EntityLeaveLevelEvent) {
    if (event.entity is Player) PlayerConnectionEvents.Leave(event.entity as Player).post()
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

@OptIn(CynosureInternal::class)
@SubscribeEvent
public fun onReloadListener(event: AddReloadListenerEvent) {
    CynosureHooksImpl.SERVER_RELOAD_LISTENERS.forEach { (_, listener) -> event.addListener(listener) }
}