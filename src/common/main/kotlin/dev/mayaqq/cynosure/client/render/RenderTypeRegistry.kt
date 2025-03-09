package dev.mayaqq.cynosure.client.render

import com.mojang.blaze3d.vertex.BufferBuilder
import dev.mayaqq.cynosure.utils.make
import net.minecraft.client.renderer.RenderType
import java.util.*

public fun RenderType.batched(phase: BatchOutputStage, builder: BufferBuilder = BufferBuilder(bufferSize())): RenderType {
    RenderTypeRegistry.registerBatched(phase, this, builder)
    return this
}

public enum class BatchOutputStage {
    ENTITY,
    BLOCK_ENTITY,
    TRANSLUCENT
}

public object RenderTypeRegistry {

    @JvmField
    @PublishedApi
    internal val TYPES: MutableMap<RenderType, BufferBuilder> = mutableMapOf()

    @JvmField
    @PublishedApi
    internal val PHASES: Map<BatchOutputStage, MutableList<RenderType>> = make(EnumMap(BatchOutputStage::class.java)) {
        BatchOutputStage.entries.forEach { put(it, ArrayList()) };
    }

    @JvmField
    @PublishedApi
    internal var frozen: Boolean = false

    public fun registerBatched(phase: BatchOutputStage, renderType: RenderType, builder: BufferBuilder = BufferBuilder(renderType.bufferSize())) {
        require(!frozen) { "Render type registry already frozon" }
        if(TYPES.containsKey(renderType)) {
            throw IllegalArgumentException("RenderType $renderType has already been registered")
        }

        TYPES[renderType] = builder
        PHASES[phase]!!.add(renderType)
    }

}
