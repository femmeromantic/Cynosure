package dev.mayaqq.cynosure.data

import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.internal.CynosureHooks
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener

@OptIn(CynosureInternal::class)
public fun registerResourcepackReloadListener(id: ResourceLocation, listener: PreparableReloadListener) {
    CynosureHooks.registerResourcepackReloadListener(id, listener)
}

@OptIn(CynosureInternal::class)
public fun registerDatapackReloadListener(id: ResourceLocation, listener: PreparableReloadListener) {
    CynosureHooks.registerDatapackReloadListener(id, listener)
}