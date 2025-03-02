package dev.mayaqq.cynosure.data

import dev.mayaqq.cynosure.utils.codecs.Either
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.FloatArraySerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.resources.ResourceLocation
import org.joml.Vector3f

private val FLOAT = Float.serializer().descriptor

object Vector3fSerializer : KSerializer<Vector3f> {
    override val descriptor: SerialDescriptor =
        SerialDescriptor("dev.mayaqq.cynosure.Vector3f", FloatArraySerializer().descriptor)

    override fun deserialize(decoder: Decoder): Vector3f = decoder.beginStructure(descriptor).let {
        Vector3f(
            it.decodeFloatElement(FLOAT, 0),
            it.decodeFloatElement(FLOAT, 1),
            it.decodeFloatElement(FLOAT, 2)
        )
    }

    override fun serialize(encoder: Encoder, value: Vector3f) {
        val structure = encoder.beginStructure(descriptor)
        structure.encodeFloatElement(FLOAT, 0, value.x)
        structure.encodeFloatElement(FLOAT, 1, value.y)
        structure.encodeFloatElement(FLOAT, 2, value.z)

        val meow: Either<Array<Float>, Vector3f> = Either.left(arrayOf(3f, 3f, 3f))
        val sum: Float = meow.map({ it[0] + it[1] + it[2] }, { it.x + it.y + it.z })
    }
}

object ResourceLocationSerializer : KSerializer<ResourceLocation> {
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.mayaqq.cynosure.ResourceLocation", String.serializer().descriptor)

    override fun deserialize(decoder: Decoder): ResourceLocation = ResourceLocation(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: ResourceLocation) {
        encoder.encodeString("${value.namespace}:${value.path}")

    }

}

