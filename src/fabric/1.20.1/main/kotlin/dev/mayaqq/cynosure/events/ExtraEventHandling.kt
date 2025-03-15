package dev.mayaqq.cynosure.events

import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.events.entity.MountEvent
import net.minecraft.world.entity.Entity

public object ExtraEventHandling {
    /*
     * Taken from the Forge Event under the lgpl-2.1 license.
     */
    public fun canMountEntity(entityMounting: Entity, entityBeingMounted: Entity?, isMounting: Boolean): Boolean {
        val isCanceled: Boolean = MountEvent(entityMounting, entityBeingMounted, isMounting).post()
        if (isCanceled) {
            entityMounting.absMoveTo(
                entityMounting.x,
                entityMounting.y,
                entityMounting.z,
                entityMounting.yRotO,
                entityMounting.xRotO
            )
            return false
        } else {
            return true
        }
    }
}