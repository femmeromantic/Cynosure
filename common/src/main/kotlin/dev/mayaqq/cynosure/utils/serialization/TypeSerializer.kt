package dev.mayaqq.cynosure.utils.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.objectweb.asm.Type

public object TypeSerializer : KSerializer<Type> {
    override val descriptor: SerialDescriptor =
        SerialDescriptor("dev.mayaqq.cynosure.serializers.Type", String.Companion.serializer().descriptor)

    override fun serialize(encoder: Encoder, value: Type) {
        encoder.encodeString(value.descriptor)
    }

    override fun deserialize(decoder: Decoder): Type = Type.getType(decoder.decodeString())
}