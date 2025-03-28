@file:Suppress("ACTUAL_WITHOUT_EXPECT")
package dev.mayaqq.cynosure.utils

import dev.mayaqq.cynosure.utils.mod.Mod
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder
import net.minecraftforge.fml.ModList

public actual fun isModLoaded(modid: String): Boolean {
    return ModList.get().isLoaded(modid)
}

public actual fun getMod(modid: String): Mod? {
    if (ModList.get().isLoaded(modid)) {
        val modContainer = ModList.get().getModContainerById(modid).get()
        return Mod(
            modid,
            modContainer.modInfo.displayName,
            modContainer.modInfo.description,
            modContainer.modInfo.version.toString()
        )
    }
    return null
}

public actual fun getLoader(modid: String): Loader {
    return Loader.FORGE
}

public actual fun AttributeSupplier.toBuilder(): Builder = Builder(this)