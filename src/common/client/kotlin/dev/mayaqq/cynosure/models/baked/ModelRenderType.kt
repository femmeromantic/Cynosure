package dev.mayaqq.cynosure.models.baked

import com.mojang.blaze3d.vertex.VertexConsumer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation

@Serializable
enum class ModelRenderType(private val func: (ResourceLocation) -> RenderType) {
    @SerialName("solid") SOLID(RenderType::entitySolid),
    @SerialName("cutout") CUTOUT(RenderType::entityCutout),
    @SerialName("translucent") TRANSLUCENT(RenderType::entityTranslucentCull),
    @SerialName("emissive") EMISSIVE(RenderType::entityTranslucentEmissive);

    fun apply(id: ResourceLocation): RenderType = func(id)
}