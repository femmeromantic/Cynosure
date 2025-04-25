package dev.mayaqq.cynosure.entities

import dev.mayaqq.cynosure.mixin.accessor.ChunkMapAccessor
import dev.mayaqq.cynosure.mixin.accessor.TrackedEntityAccessor
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import net.minecraft.server.level.ChunkMap
import net.minecraft.server.level.ServerChunkCache
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerPlayerConnection
import net.minecraft.world.entity.Entity
import java.util.*
import java.util.stream.Collectors

/*
 * This code includes modifications based on or derived from code provided by fabric-api.
 * The original code can be found at: https://github.com/FabricMC/fabric
 * fabric-api is licensed under Apache License 2.0.
 */
public object PlayerLookup {
    public fun tracking(entity: Entity?): MutableCollection<ServerPlayer?> {
        Objects.requireNonNull<Entity?>(entity, "Entity cannot be null")
        val manager = entity!!.level().chunkSource

        if (manager is ServerChunkCache) {
            val storage = manager.chunkMap
            val tracker = storage.getEntityMap().get(entity.id)

            // return an immutable collection to guard against accidental removals.
            if (tracker != null) {
                return tracker.getPlayersTracking().stream().map<ServerPlayer?> { obj: ServerPlayerConnection? -> obj!!.player }
                    .collect(Collectors.toUnmodifiableSet())
            }

            return mutableSetOf<ServerPlayer?>()
        }

        throw IllegalArgumentException("Only supported on server worlds!")
    }
}

public inline fun ChunkMap.getEntityMap(): Int2ObjectMap<TrackedEntityAccessor> = (this as ChunkMapAccessor).getEntityMap()