package dev.mayaqq.cynosure

import dev.mayaqq.cynosure.client.events.command.CommandRegistrationEvent
import dev.mayaqq.cynosure.events.PostInitEvent
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.events.gatherEventSubscribers
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PathPackResources
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.PackSource
import net.minecraftforge.event.AddPackFindersEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.forgespi.locating.IModFile
import java.util.function.Consumer

@Mod(MODID)
//@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public object CynosureForge {
    init {
        gatherEventSubscribers()
        Cynosure.init()
    }

    @SubscribeEvent
    public fun lateInit(event: FMLCommonSetupEvent) {
        event.enqueueWork(PostInitEvent::post)
    }

    @SubscribeEvent
    public fun onCommandRegistration(event: RegisterCommandsEvent) {
        CommandRegistrationEvent(event.dispatcher, event.buildContext, event.commandSelection).post()
    }

    @SubscribeEvent
    public fun addPackFinders(event: AddPackFindersEvent) {
        for (mod in ModList.get().mods) {
            val metadata = mod.modProperties.getCynosureValue("resourcepacks") as? List<*> ?: continue
            try {
                for (pack in metadata) {
                    when (pack) {
                        is String -> event.createPack(mod.owningFile.file, ResourceLocation(mod.modId, pack))
                    }
                }
            } catch (ex: Exception) {
                Cynosure.error("Failed to load pack for mod ${mod.modId}")
            }
        }
    }

    private fun AddPackFindersEvent.createPack(modFile: IModFile, id: ResourceLocation) {
        val resourcePath = modFile.findResource("resourcepacks/$id")

        Pack.readMetaAndCreate(
            "${id.namespace}/${id.path}",
            Component.translatable(id.toLanguageKey("resourcepack")),
            false,
            { path -> PathPackResources(path, resourcePath, true) },
            packType, Pack.Position.TOP, PackSource.BUILT_IN
        )?.let { addRepositorySource { consumer -> consumer.accept(it) } }
    }

    private fun Map<String, Any>.getCynosureValue(key: String): Any? {
        if (containsKey("cynosure:$key")) {
            return get("cynosure:$key")
        } else {
            if (containsKey("cynosure")) {
                val cynosureData = get("cynosure") as Map<String, Any>
                return cynosureData[key]
            }
        }
        return null
    }
}