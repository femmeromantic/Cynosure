package dev.mayaqq.cynosure.internal

import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.injection.IAttributeSupplier
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

@CynosureInternal
internal object CynosureHooksImpl : CynosureHooks {
    override fun attributeSupplierToBuilder(supplier: AttributeSupplier): AttributeSupplier.Builder {
        return (supplier as IAttributeSupplier).cynosure_toBuilder()
    }

    override fun registerResourcepackReloadListener(id: ResourceLocation, listener: PreparableReloadListener) {
        registerReloadListener(id, PackType.CLIENT_RESOURCES, listener)
    }

    override fun registerDatapackReloadListener(id: ResourceLocation, listener: PreparableReloadListener) {
        registerReloadListener(id, PackType.SERVER_DATA, listener)
    }

    private fun registerReloadListener(id: ResourceLocation, type: PackType, listener: PreparableReloadListener) {
        val identifiable = object : IdentifiableResourceReloadListener {
            override fun reload(
                preparationBarrier: PreparableReloadListener.PreparationBarrier,
                resourceManager: ResourceManager,
                profilerFiller: ProfilerFiller,
                profilerFiller2: ProfilerFiller,
                executor: Executor,
                executor2: Executor
            ): CompletableFuture<Void> {
                return listener.reload(preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2)
            }

            override fun getFabricId(): ResourceLocation = id
        }

        ResourceManagerHelper.get(type).registerReloadListener(identifiable)
    }
}