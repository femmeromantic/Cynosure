@file:Suppress("ACTUAL_WITHOUT_EXPECT")
package dev.mayaqq.cynosure.utils

import dev.mayaqq.cynosure.injection.IAttributeSupplier
import dev.mayaqq.cynosure.utils.mod.Mod
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder

public actual fun isModLoaded(modid: String): Boolean {
    return FabricLoader.getInstance().isModLoaded(modid)
}

public actual fun getMod(modid: String): Mod? {
    if (FabricLoader.getInstance().isModLoaded(modid)) {
        val modContainer = FabricLoader.getInstance().getModContainer(modid).get()
        return Mod(
            modid,
            modContainer.metadata.name,
            modContainer.metadata.description,
            modContainer.metadata.version.friendlyString
        )
    }
    return null
}

public actual fun getLoader(modid: String): Loader {
    return Loader.FABRIC
}

public actual fun AttributeSupplier.toBuilder(): Builder = (this as IAttributeSupplier).cynosure_toBuilder()