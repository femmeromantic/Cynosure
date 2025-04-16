package dev.mayaqq.cynosure.events

import dev.mayaqq.cynosure.events.api.EventSubscriber
import dev.mayaqq.cynosure.events.api.EventBus
import dev.mayaqq.cynosure.events.api.MainBus
import dev.mayaqq.cynosure.events.api.subscribeTo
import dev.mayaqq.cynosure.utils.PlatformHooks
import net.minecraftforge.fml.ModList
import org.objectweb.asm.Type

internal val SUB_ANNOTATION_TYPE = Type.getType(EventSubscriber::class.java)

internal fun gatherEventSubscribers() {
    for(scandata in ModList.get().allScanData) {
        for(annotation in scandata.annotations) {
            if(annotation.annotationType != SUB_ANNOTATION_TYPE) continue
            try {
                // TODO: Sided event handleers
                //val side = annotation.annotationData["env"]
                //if (side != null && side != PlatformHooks.environment) continue

                val bus = (annotation.annotationData["bus"] as? String)
                    ?.let { Class.forName(it).let {
                        (it.kotlin.objectInstance ?: it.getConstructor(String::class.java).newInstance(scandata.iModInfoData[0].mods[0].modId)) as? EventBus
                    } } ?: MainBus

                // TODO: Use asm handler generator on forge to avoid class loading
                val clazz = Class.forName(annotation.memberName)
                clazz.kotlin.objectInstance?.subscribeTo(bus) ?: clazz.subscribeTo(bus)
            } catch(e: ReflectiveOperationException) {

                continue
            }
        }
    }
}