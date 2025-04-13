package dev.mayaqq.cynosure.utils.serialization.defaults

import dev.mayaqq.cynosure.utils.serialization.buildClassSerializer
import dev.mayaqq.cynosure.utils.serialization.fieldOf
import dev.mayaqq.cynosure.utils.serialization.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import net.minecraft.resources.ResourceLocation
import org.joml.Vector3f
import org.joml.Vector3fc

public object ResourceLocationSerializer : KSerializer<ResourceLocation> by String.serializer().map(::ResourceLocation, ResourceLocation::toString)

public object Vector3fSerializer : KSerializer<Vector3f> by buildClassSerializer("",
    Float.serializer().fieldOf("x", Vector3fc::x),
    Float.serializer().fieldOf("y", Vector3fc::y),
    Float.serializer().fieldOf("z", Vector3fc::z),
    ::Vector3f
)