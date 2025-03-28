package dev.mayaqq.cynosure.entities

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.msrandom.stub.Stub

/**
 * Utilities for registering and modifying entity attributes
 */
@Stub
public expect object EntityAttributes {

    /**
     * Register attributes for this entity type. This will crash if the entitty already has attributes
     * @param supplier the entity attribute supplier
     * @throws IllegalStateException if the entity type already has attributes registered
     */
    public fun register(
        entity: EntityType<out LivingEntity>,
        supplier: AttributeSupplier,
    )

    /**
     * Add an attribute to this entity
     * @param attribute The attribute to add
     * @param value The value of the attribute, defaults to [Attribute.defaultValue]
     */
    public fun addToEntity(
        entity: EntityType<out LivingEntity>,
        attribute: Attribute,
        value: Double = attribute.defaultValue
    )

    /**
     * Add all the given attributes to this entity
     * @param builder the builder u can modify
     */
    public fun modify(
        entity: EntityType<out LivingEntity>,
        builder: AttributeSupplier.Builder.() -> Unit
    )
}

