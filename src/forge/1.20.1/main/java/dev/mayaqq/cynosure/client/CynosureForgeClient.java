package dev.mayaqq.cynosure.client;

import dev.mayaqq.cynosure.Cynosure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Cynosure.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CynosureForgeClient {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
    }
}
