package dev.mayaqq.cynosure.utils

import com.google.common.collect.BiMap
import net.minecraft.resources.ResourceLocation

public class SerializationRegistry<T>(
    val byId: BiMap<ResourceLocation, T>
) {

}