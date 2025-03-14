package dev.mayaqq.cynosure.client

import com.mojang.blaze3d.systems.RenderSystem
import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.MODID
import dev.mayaqq.cynosure.client.events.RegisterParticleFactoriesEvent
import dev.mayaqq.cynosure.client.render.gui.OverlayRegistry
import dev.mayaqq.cynosure.client.render.gui.VanillaHud
import dev.mayaqq.cynosure.events.api.post
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent
import net.minecraftforge.client.event.RegisterParticleProvidersEvent
import net.minecraftforge.client.gui.overlay.ForgeGui
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
public object CynosureForgeClient {
    @SubscribeEvent
    public fun clientSetup(event: FMLClientSetupEvent?) {
        CynosureClient.init()
    }

    @SubscribeEvent
    public fun registerParticles(event: RegisterParticleProvidersEvent) {
        object : RegisterParticleFactoriesEvent() {
            override fun <T : ParticleOptions> register(type: ParticleType<T>, provider: ParticleProvider<T>) {
                event.registerSpecial(type, provider)
            }

            override fun <T : ParticleOptions> register(type: ParticleType<T>, factoryProvider: (SpriteSet) -> ParticleProvider<T>) {
                event.registerSpriteSet(type, factoryProvider)
            }
        }.post()
    }

    @OptIn(CynosureInternal::class)
    @SubscribeEvent
    public fun registerGuiOverlays(event: RegisterGuiOverlaysEvent) {
        VanillaHud.entries.forEach {
            event.registerBelow(it.forgeId, "cynosure_overlays_${it.forgeId}") { forgeGui: ForgeGui, guiGraphics: GuiGraphics, fl: Float, i: Int, i1: Int ->
                RenderSystem.enableBlend()
                RenderSystem.disableDepthTest()
                OverlayRegistry.sorted[it]?.forEach { overlay -> overlay.render(forgeGui, guiGraphics, fl) }
            }
        }
    }
}