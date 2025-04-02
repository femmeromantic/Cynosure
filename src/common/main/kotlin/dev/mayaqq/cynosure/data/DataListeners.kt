package dev.mayaqq.cynosure.data

import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManagerReloadListener

public object DataListeners {
    private var locked = false
    private var listeners = mutableListOf<DataListener>()

    public fun addListener(data: DataListener) {
        if (locked) throw IllegalStateException("Cannot add server listener after server lock.")
        listeners.add(data)
    }

    public fun lock() {
        locked = true
    }

    // lambda function register that calls the supplied lambda function with the register function as argument
    public fun register(register: (DataListener) -> Unit) {
        for (listener in listeners) register(listener)
    }
}

public abstract class DataListener(public val id: ResourceLocation) : ResourceManagerReloadListener