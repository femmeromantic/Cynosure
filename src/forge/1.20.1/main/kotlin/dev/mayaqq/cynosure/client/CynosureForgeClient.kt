package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.MODID
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
public object CynosureForgeClient {
    @SubscribeEvent
    public fun clientSetup(event: FMLClientSetupEvent?) {
        CynosureClient.init()
    }
}