package dev.mayaqq.cynosure.internal

import dev.mayaqq.cynosure.CynosureInternal
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.world.entity.ai.attributes.AttributeSupplier

@CynosureInternal
public interface CynosureHooks {

    public companion object : CynosureHooks by loadPlatform()

    public fun attributeSupplierToBuilder(supplier: AttributeSupplier): AttributeSupplier.Builder

    public fun registerResourcepackReloadListener(id: ResourceLocation, listener: PreparableReloadListener)

    public fun registerDatapackReloadListener(id: ResourceLocation, listener: PreparableReloadListener)

}