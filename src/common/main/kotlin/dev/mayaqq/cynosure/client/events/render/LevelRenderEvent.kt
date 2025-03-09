package dev.mayaqq.cynosure.client.events.render

import com.mojang.blaze3d.vertex.PoseStack
import dev.mayaqq.cynosure.events.api.Event
import dev.mayaqq.cynosure.events.api.RootEventClass
import net.minecraft.client.Camera
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.HitResult

/**
 * Invoked when the level renderer reloads
 */
public object ReloadLevelRendererEvent : Event

@RootEventClass
public sealed class LevelRenderEvent(
    public val renderer: LevelRenderer,
    public val poseStack: PoseStack,
    public val partialTick: Float,
    public val camera: Camera,
    public val frustum: Frustum?,
    public val bufferSource: MultiBufferSource?
) : Event {

    /**
     * Fired at the beginning of [LevelRenderer.renderLevel].
     * Note that frustum and bufferSource will not yet be available in this event
     */
    public class Start(
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ): LevelRenderEvent(renderer, poseStack, partialTick, camera, frustum, bufferSource)

    /**
     * Fired Aftter terrain rendering setup is complete but before any terrain is rendered
     */
    public class BeforeTerrain(
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : LevelRenderEvent(renderer, poseStack, partialTick, camera, frustum, bufferSource)

    /**
     * Fired after solid, cutout and cutout mipped render layers have been rendered but before any entity rendering occurs
     */
    public class AfterTerrain(
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : LevelRenderEvent(renderer, poseStack, partialTick, camera, frustum, bufferSource)

    /**
     * Fired after entities are rendered but before block entities
     */
    public class AfterEntities(
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : LevelRenderEvent(renderer, poseStack, partialTick, camera, frustum, bufferSource)

    public class BeforeBlockOutline(
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?,
        public val hitResult: HitResult?
    ) : LevelRenderEvent(renderer, poseStack, partialTick, camera, frustum, bufferSource) {
        public var renderOutline: Boolean = true
        private set

        public fun noBlockOutline() {
            renderOutline = false
        }
    }

    public class BlockOutline(
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?,
        public val entity: Entity,
        public val pos: BlockPos,
        public val state: BlockState
    ) : LevelRenderEvent(renderer, poseStack, partialTick, camera, frustum, bufferSource) {
        public var renderVanillaOutline: Boolean = true
        private set

        public fun noVanillaOutline() {
            renderVanillaOutline = false
        }
    }

    public class DebugRender(
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?,
    ) : LevelRenderEvent(renderer, poseStack, partialTick, camera, frustum, bufferSource)

    public class AfterTranslucentTerrain(
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : LevelRenderEvent(renderer, poseStack, partialTick, camera, frustum, bufferSource)

    public class AfterParticles(
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : LevelRenderEvent(renderer, poseStack, partialTick, camera, frustum, bufferSource)

    public class Last(
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : LevelRenderEvent(renderer, poseStack, partialTick, camera, frustum, bufferSource)

    public class End(
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : LevelRenderEvent(renderer, poseStack, partialTick, camera, frustum, bufferSource)
}