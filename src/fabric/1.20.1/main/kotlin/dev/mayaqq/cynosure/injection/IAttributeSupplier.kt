package dev.mayaqq.cynosure.injection

import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder

internal interface IAttributeSupplier {
    fun cynosure_toBuilder(): Builder
}

public fun AttributeSupplier.toBuilder(): Builder = (this as IAttributeSupplier).cynosure_toBuilder()