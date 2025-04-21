package dev.mayaqq.cynosure.keymapping

import dev.mayaqq.cynosure.Cynosure
import dev.mayaqq.cynosure.MODID
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import org.apache.commons.lang3.ArrayUtils

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
internal object KeyMappingRegistryImpl : KeyMappingRegistry {

    private val mappings = mutableListOf<KeyMapping>()
    private var registered = false

    /**
     * Register a key mapping
     * @param mapping the key mapping to register
     */
    override fun register(mapping: KeyMapping) {
        if (registered) {
            val options = Minecraft.getInstance().options
            options.keyMappings = ArrayUtils.add(options.keyMappings, mapping)
            Cynosure.warn("KeyMapping Added after Forge Event Fired, possible but not recommended")
        } else {
            mappings.add(mapping)
        }
    }

    @SubscribeEvent
    fun onKeyMappingRegister(event: RegisterKeyMappingsEvent) {
        mappings.forEach(event::register)
        registered = true
    }
}