package dev.mayaqq.cynosure.utils

import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.internal.CynosureHooks
import net.minecraft.world.entity.ai.attributes.AttributeSupplier

@OptIn(CynosureInternal::class)
public fun AttributeSupplier.toBuilder(): AttributeSupplier.Builder = CynosureHooks.attributeSupplierToBuilder(this)