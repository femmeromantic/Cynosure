package dev.mayaqq.cynosure.client.events.entity

import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.client.utils.DefaultSkin
import dev.mayaqq.cynosure.events.api.Event
import dev.mayaqq.cynosure.mixin.client.accessor.LivingEntityRendererAccessor
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.EntityModelSet
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import org.jetbrains.annotations.ApiStatus.NonExtendable

@OptIn(CynosureInternal::class)
public class RenderLayerRegistrationEvent(
    public val renderers: EntityRenderDispatcher,
    public val models: EntityModelSet,
    private val context: Context
) : Event {

    public fun <T : LivingEntity> addLayer(entity: EntityType<T>, layer: RenderLayer<T, out EntityModel<T>>) {
        (context.getEntity(entity) as? LivingEntityRendererAccessor)?.addLayer(layer)
    }

    public fun addLayer(skin: DefaultSkin, layer: RenderLayer<out Player, out EntityModel<out Player>>) {
        addLayer(skin.skinName, layer)
    }

    public fun addLayer(skin: String, layer: RenderLayer<out Player, out EntityModel<out Player>>) {
        (context.getSkin(skin) as? LivingEntityRendererAccessor)?.addLayer(layer)
    }

    @CynosureInternal
    @NonExtendable
    public interface Context {

        public fun getSkin(name: String): EntityRenderer<out Player>?

        public fun <T : LivingEntity> getEntity(entity: EntityType<T>): LivingEntityRenderer<in T, *>?
    }
}