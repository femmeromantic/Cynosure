@file:Suppress("ACTUAL_WITHOUT_EXPECT")
package dev.mayaqq.cynosure.utils

import dev.mayaqq.cynosure.utils.mod.Mod
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder
import net.minecraftforge.fml.ModList
import kotlin.jvm.optionals.getOrNull

public object PlatformHooksImpl : PlatformHooks {
    override fun isModLoaded(modid: String): Boolean = ModList.get().isLoaded(modid)

    override fun getMod(modid: String): Mod? {
        val modContainer = ModList.get().getModContainerById(modid).getOrNull()
        if (modContainer != null) {
            return Mod(
                modid,
                modContainer.modInfo.displayName,
                modContainer.modInfo.description,
                modContainer.modInfo.version.toString()
            )
        }
        return null
    }

    override fun currentLoader(): Loader = Loader.FORGE
}