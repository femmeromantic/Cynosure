@file:UseSerializers(Vector3fSerializer::class)

package dev.mayaqq.cynosure.client.models

import dev.mayaqq.cynosure.utils.codecs.Either
import dev.mayaqq.cynosure.client.data.ResourceLocationSerializer
import dev.mayaqq.cynosure.client.data.Vector3fSerializer
import dev.mayaqq.cynosure.client.models.baked.ModelRenderType
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import org.joml.Vector3f


@Serializable
public data class ModelElementRotation(
    val angle: Float,
    val axis: Direction.Axis,
    val origin: Vector3f,
    val rescale: Boolean
)

@Serializable
public data class ModelElementFace(
    val texture: String,
    val uv: FloatArray,
    val rotation: Float
) {
    init {
        require(uv.size == 4) { "UV array has to contain exactly 4 elements" }
    }

    private fun getShiftedIndex(index: Int): Int = ((index + rotation / 90) % 4).toInt()

    public fun getU(index: Int): Float {
        val i: Int = getShiftedIndex(index)
        return this.uv[if (i != 0 && i != 1) 2 else 0] / 16f
    }

    public fun getV(index: Int): Float {
        val i: Int = getShiftedIndex(index)
        return this.uv[if (i != 0 && i != 3) 3 else 1] / 16f
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModelElementFace

        if (rotation != other.rotation) return false
        if (texture != other.texture) return false
        if (!uv.contentEquals(other.uv)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rotation.hashCode()
        result = 31 * result + texture.hashCode()
        result = 31 * result + uv.contentHashCode()
        return result
    }
}

@Serializable
public data class ModelElement(
    val from: Vector3f,
    val to: Vector3f,
    val faces: Map<Direction, ModelElementFace>,
    val rotation: ModelElementRotation? = null,
    val shade: Boolean = true
)

@Serializable
public data class ModelElementGroup(
    val name: String,
    val renderType: ModelRenderType? = null,
    val origin: Vector3f,
    val elements: List<Either<Int, ModelElementGroup>>
)

public inline val ModelElementGroup.indices: List<Int>
    get() = elements.mapNotNull { it.left }

public inline val ModelElementGroup.subgroups: List<ModelElementGroup>
    get() = elements.mapNotNull { it.right }

@Serializable
public data class ModelData(
    val renderType: ModelRenderType = ModelRenderType.CUTOUT,
    val textures: Map<String, @Serializable(ResourceLocationSerializer::class) ResourceLocation>,
    val elements: List<ModelElement>,
    val groups: List<ModelElementGroup>
)

@OptIn(ExperimentalSerializationApi::class)
public fun ModelData.Companion.fromJson(json: JsonElement): Result<ModelData> = runCatching { Json.decodeFromJsonElement(json) }

