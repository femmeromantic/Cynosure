package dev.mayaqq.cynosure.client.events

import com.mojang.blaze3d.vertex.VertexFormat
import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.resources.ResourceLocation
import org.jetbrains.annotations.ApiStatus.NonExtendable

@OptIn(CynosureInternal::class)
public class ParticleFactoryRegistrationEvent(private val context: Context) : Event {

    public fun <T : ParticleOptions> register(type: ParticleType<T>, provider: ParticleProvider<T>) {
        context.register(type, provider)
    }

    public fun <T : ParticleOptions> register(type: ParticleType<T>, factoryProvider: (SpriteSet) -> ParticleProvider<T>) {
        context.register(type, factoryProvider)
    }

    @NonExtendable
    @CynosureInternal
    public interface Context {
        public fun <T : ParticleOptions> register(type: ParticleType<T>, provider: ParticleProvider<T>)

        public fun <T : ParticleOptions> register(type: ParticleType<T>, factoryProvider: (SpriteSet) -> ParticleProvider<T>)
    }
}

@OptIn(CynosureInternal::class)
public class CoreShaderRegistrationEvent(private val context: Context) : Event {

    public fun register(
        id: ResourceLocation,
        format: VertexFormat,
        onLoad: (ShaderInstance) -> Unit
    ) {
        context.register(id, format, onLoad)
    }

    @NonExtendable
    @CynosureInternal
    public fun interface Context {
        public fun register(id: ResourceLocation, format: VertexFormat, onLoad: (ShaderInstance) -> Unit)
    }
}

public sealed class ClientTickEvent : Event {

    /**
     * Fired at the beginning of each client tick
     */
    public data object Begin : ClientTickEvent()

    /**
     * Fired at the end of each client tick
     */
    public data object End: ClientTickEvent()
}
