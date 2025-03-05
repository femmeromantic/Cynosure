package dev.mayaqq.cynosure

import dev.mayaqq.cynosure.events.LateInitEvent
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.events.gatherEventSubscribers
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

@Mod(MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public object CynosureForge {
    init {
        gatherEventSubscribers()
        Cynosure.init()
    }

    @SubscribeEvent
    public fun lateInit(event: FMLCommonSetupEvent) {
        event.enqueueWork(LateInitEvent::post)
    }
}