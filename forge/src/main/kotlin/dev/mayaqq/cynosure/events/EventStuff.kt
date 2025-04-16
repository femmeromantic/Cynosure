package dev.mayaqq.cynosure.events

import codes.som.koffee.modifiers.annotation
import dev.mayaqq.cynosure.events.api.EventSubscriber
import dev.mayaqq.cynosure.events.api.EventBus
import dev.mayaqq.cynosure.events.api.MainBus
import dev.mayaqq.cynosure.events.api.Subscription
import dev.mayaqq.cynosure.events.api.subscribeTo
import dev.mayaqq.cynosure.utils.PlatformHooks
import net.minecraftforge.fml.ModList
import org.objectweb.asm.Type

internal val SUB_ANNOTATION_TYPE = Type.getType(EventSubscriber::class.java)
internal val SUBSCRIPTION = Type.getType(Subscription::class.java)

internal fun gatherEventSubscribers() {
    for(scandata in ModList.get().allScanData) {

        val subscribers: MutableMap<String, EventBus> = mutableMapOf()

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

                subscribers.put(annotation.memberName, bus)
            } catch(e: ReflectiveOperationException) {
                continue
            }
        }
        for (annotation in scandata.annotations.filter { it.annotationType ==  SUBSCRIPTION}) {
            val className = annotation.clazz.className
            val bus = subscribers[className] ?: continue


        }
    }


}