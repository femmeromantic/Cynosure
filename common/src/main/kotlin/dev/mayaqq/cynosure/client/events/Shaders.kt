package dev.mayaqq.cynosure.client.events

import com.mojang.blaze3d.vertex.VertexFormat
import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.resources.ResourceLocation
import org.jetbrains.annotations.ApiStatus

@OptIn(CynosureInternal::class)
public class CoreShaderRegistrationEvent(private val context: Context) : Event {

    public fun register(
        id: ResourceLocation,
        format: VertexFormat,
        onLoad: (ShaderInstance) -> Unit
    ) {
        context.register(id, format, onLoad)
    }

    @ApiStatus.NonExtendable
    @CynosureInternal
    public fun interface Context {
        public fun register(id: ResourceLocation, format: VertexFormat, onLoad: (ShaderInstance) -> Unit)
    }
}