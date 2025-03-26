package dev.mayaqq.cynosure

import dev.mayaqq.cynosure.events.FapiFeed
import dev.mayaqq.cynosure.events.LateInitEvent
import dev.mayaqq.cynosure.events.api.post
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.fabric.mixin.client.rendering.WorldRendererMixin
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.DefaultAttributes

public object CynosureFabric {
    public fun init() {
        Cynosure.init()
        FapiFeed.feed()
    }

    public fun lateinit() {
        LateInitEvent.post()
    }
}