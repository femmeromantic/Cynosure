@file:EventBusSubscriber(value = [Dist.CLIENT], modid = MODID)
package dev.mayaqq.cynosure.client.events

import dev.mayaqq.cynosure.MODID
import dev.mayaqq.cynosure.client.events.render.LevelRenderEvent
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.forge.mixin.client.LevelRendererAccessor
import net.minecraft.client.renderer.LevelRenderer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

internal val LevelRenderer.renderBuffers get() = (this as LevelRendererAccessor).renderBuffers
internal val RenderLevelStageEvent.bufferSource get() = this.levelRenderer.renderBuffers.bufferSource()

@SubscribeEvent
public fun onLevelRender(event: RenderLevelStageEvent) {
    when(event.stage) {
        RenderLevelStageEvent.Stage.AFTER_SKY -> LevelRenderEvent.BeforeTerrain(
            event.levelRenderer, event.poseStack, event.partialTick,
            event.camera, event.frustum, event.bufferSource
        ).post()
        RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS -> LevelRenderEvent.AfterTerrain(
            event.levelRenderer, event.poseStack, event.partialTick,
            event.camera, event.frustum, event.bufferSource
        ).post()
        RenderLevelStageEvent.Stage.AFTER_ENTITIES -> LevelRenderEvent.AfterEntities(
            event.levelRenderer, event.poseStack, event.partialTick,
            event.camera, event.frustum, event.bufferSource
        ).post()
        RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS -> LevelRenderEvent.AfterTranslucentTerrain(
            event.levelRenderer, event.poseStack, event.partialTick,
            event.camera, event.frustum, event.bufferSource
        ).post()
        RenderLevelStageEvent.Stage.AFTER_PARTICLES -> LevelRenderEvent.AfterParticles(
            event.levelRenderer, event.poseStack, event.partialTick,
            event.camera, event.frustum, event.bufferSource
        ).post()
        RenderLevelStageEvent.Stage.AFTER_WEATHER -> LevelRenderEvent.Last(
            event.levelRenderer, event.poseStack, event.partialTick,
            event.camera, event.frustum, event.bufferSource
        ).post()
        RenderLevelStageEvent.Stage.AFTER_LEVEL -> LevelRenderEvent.End(
            event.levelRenderer, event.poseStack, event.partialTick,
            event.camera, null, null
        ).post()
    }
}

