package dev.mayaqq.cynosure.events

import dev.mayaqq.cynosure.events.api.AutoSubscriber
import dev.mayaqq.cynosure.events.api.EventBus
import dev.mayaqq.cynosure.events.api.MainBus
import dev.mayaqq.cynosure.events.api.subscribeTo
import net.minecraftforge.fml.ModList
import org.objectweb.asm.Type

internal val SUB_ANNOTATION_TYPE = Type.getType(AutoSubscriber::class.java)

internal fun gatherEventSubscribers() {
    for(scandata in ModList.get().allScanData) {
        for(annotation in scandata.annotations) {
            if(annotation.annotationType != SUB_ANNOTATION_TYPE) continue
            try {
                val bus = (annotation.annotationData["bus"] as? String)
                    ?.let { Class.forName(it).kotlin.objectInstance!! as EventBus }
                    ?: MainBus

                val clazz = Class.forName(annotation.memberName)
                clazz.kotlin.objectInstance?.subscribeTo(bus) ?: clazz.subscribeTo(bus)
            } catch(e: ReflectiveOperationException) {

                continue
            }
        }
    }
}