package dev.mayaqq.cynosure.utils

import dev.mayaqq.cynosure.utils.mod.Mod
import net.fabricmc.loader.api.FabricLoader


public object PlatformHooksImpl : PlatformHooks {
    override fun isModLoaded(modid: String): Boolean {
        return FabricLoader.getInstance().isModLoaded(modid)
    }

    override fun getMod(modid: String): Mod? {
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

    override fun currentLoader(): Loader = Loader.FABRIC
}