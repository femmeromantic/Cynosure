package dev.mayaqq.cynosure.client.events

import dev.mayaqq.cynosure.events.api.Event
import dev.mayaqq.cynosure.events.world.LevelEvent
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType

public abstract class RegisterParticleFactoriesEvent : Event {

    public abstract fun <T : ParticleOptions> register(type: ParticleType<T>, provider: ParticleProvider<T>)

    public abstract fun <T : ParticleOptions> register(type: ParticleType<T>, factoryProvider: (SpriteSet) -> ParticleProvider<T>)

}

public sealed class ClientTickEvent : Event {

    public data object Start : ClientTickEvent()

    public data object End: ClientTickEvent()
}
