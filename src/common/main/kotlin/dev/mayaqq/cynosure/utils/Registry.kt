package dev.mayaqq.cynosure.utils

import com.google.common.collect.BiMap
import net.minecraft.resources.ResourceLocation

public class Registry<T>(
    val byId: BiMap<ResourceLocation, T>
) {

}