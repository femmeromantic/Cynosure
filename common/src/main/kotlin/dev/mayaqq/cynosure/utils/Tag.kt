package dev.mayaqq.cynosure.utils

import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.material.Fluid

public inline fun itemTag(value: ResourceLocation): TagKey<Item> = Registries.ITEM.tag(value)
public inline fun blockTag(value: ResourceLocation): TagKey<Block> = Registries.BLOCK.tag(value)
public inline fun damageTypeTag(value: ResourceLocation): TagKey<DamageType> = Registries.DAMAGE_TYPE.tag(value)
public inline fun entityTypeTag(value: ResourceLocation): TagKey<EntityType<*>> = Registries.ENTITY_TYPE.tag(value)
public inline fun fluidTag(value: ResourceLocation): TagKey<Fluid> = Registries.FLUID.tag(value)

public fun <T> ResourceKey<Registry<T>>.tag(value: ResourceLocation): TagKey<T> = TagKey.create(this, value)