package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.CynosureFabric
import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.client.events.CynosureWorldRenderEventHandler
import dev.mayaqq.cynosure.client.events.RegisterParticleFactoriesEvent
import dev.mayaqq.cynosure.events.LateInitEvent
import dev.mayaqq.cynosure.events.api.post
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.loader.api.ModContainer
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType


public object CynosureClientFabric {
    public fun init() {
        CynosureFabric.lateinit()
        CynosureWorldRenderEventHandler.init()
        ClientFapiFeed.feed()

        object : RegisterParticleFactoriesEvent() {
            override fun <T : ParticleOptions> register(
                type: ParticleType<T>, factoryProvider: (SpriteSet) -> ParticleProvider<T>
            ) {
                ParticleFactoryRegistry.getInstance().register(type, factoryProvider)
            }

            override fun <T : ParticleOptions> register(type: ParticleType<T>, provider: ParticleProvider<T>) {
                ParticleFactoryRegistry.getInstance().register(type, provider)
            }
        }.post()

        CynosureClient.init()
    }
}