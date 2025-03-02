package dev.mayaqq.cynosure.client.models.baked

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation

@Serializable
public enum class ModelRenderType(private val func: (ResourceLocation) -> RenderType) {
    @SerialName("solid") SOLID(RenderType::entitySolid),
    @SerialName("cutout") CUTOUT(RenderType::entityCutout),
    @SerialName("translucent") TRANSLUCENT(RenderType::entityTranslucentCull),
    @SerialName("emissive") EMISSIVE(RenderType::entityTranslucentEmissive);

    public fun apply(id: ResourceLocation): RenderType = func(id)
}