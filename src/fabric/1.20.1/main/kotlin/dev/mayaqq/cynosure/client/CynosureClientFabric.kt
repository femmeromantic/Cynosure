package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.client.events.CynosureWorldRenderEventHandler
import dev.mayaqq.cynosure.client.events.RegisterParticleFactoriesEvent
import dev.mayaqq.cynosure.events.LateInitEvent
import dev.mayaqq.cynosure.events.api.post
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType


public object CynosureClientFabric {
    @OptIn(CynosureInternal::class)
    public fun init() {
        LateInitEvent.post()
        CynosureWorldRenderEventHandler.init()
        CynosureClientFabricEvents.init()

        RegisterParticleFactoriesEvent(object : RegisterParticleFactoriesEvent.ParticleFactoryRegistrator {
            override fun <T : ParticleOptions> registerPending(
                type: ParticleType<T>, factoryProvider: RegisterParticleFactoriesEvent.PendingProvider<T>
            ) {
                ParticleFactoryRegistry.getInstance().register(type, factoryProvider::create)
            }

            override fun <T : ParticleOptions> register(type: ParticleType<T>, provider: ParticleProvider<T>) {
                ParticleFactoryRegistry.getInstance().register(type, provider)
            }
        }).post()

        CynosureClient.init()
    }
}