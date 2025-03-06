package dev.mayaqq.cynosure.client.models.animations

import com.google.common.collect.HashBiMap
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.resources.ResourceLocation
import org.joml.Vector3fc
import java.util.*
import java.util.function.Supplier

public interface Animatable {
    public fun offsetPosition(offset: Vector3fc)

    public fun offsetRotation(offset: Vector3fc)

    public fun offsetScale(offset: Vector3fc)

    public fun reset()

    public interface Provider {
        public fun getAny(key: String): Animatable?
    }
}