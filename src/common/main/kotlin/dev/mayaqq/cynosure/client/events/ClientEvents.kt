package dev.mayaqq.cynosure.client.events

import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType

@OptIn(CynosureInternal::class)
public class RegisterParticleFactoriesEvent(
    private val registrator: ParticleFactoryRegistrator
) : Event {

    public fun <T : ParticleOptions> register(type: ParticleType<T>, provider: ParticleProvider<T>) {
        registrator.register(type, provider)
    }

    public fun <T : ParticleOptions> register(type: ParticleType<T>, provider: PendingProvider<T>) {
        registrator.registerPending(type, provider)
    }

    @CynosureInternal
    public interface ParticleFactoryRegistrator {
        public fun <T : ParticleOptions> registerPending(type: ParticleType<T>, factoryProvider: PendingProvider<T>)

        public fun <T : ParticleOptions> register(type: ParticleType<T>, provider: ParticleProvider<T>)
    }

    public fun interface PendingProvider<T : ParticleOptions> {
        public fun create(spriteSet: SpriteSet): ParticleProvider<T>
    }

}