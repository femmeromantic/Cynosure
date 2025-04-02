package dev.mayaqq.cynosure.data

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

public fun DataListener.toFabric() : IdentifiableResourceReloadListener {
    return FabricDataListener(this)
}

public data class FabricDataListener(val listener: DataListener) : IdentifiableResourceReloadListener {
    override fun reload(
        preparationBarrier: PreparableReloadListener.PreparationBarrier,
        resourceManager: ResourceManager,
        preparationsProfiler: ProfilerFiller,
        reloadProfiler: ProfilerFiller,
        backgroundExecutor: Executor,
        gameExecutor: Executor
    ): CompletableFuture<Void> = listener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor)

    override fun getFabricId(): ResourceLocation = listener.id
}