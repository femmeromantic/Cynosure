package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.client.events.render.EndHudRenderEvent
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.Minecraft

internal object CynosureClientFabricEvents {
    fun init() {
        HudRenderCallback.EVENT.register { graphics, float ->
            EndHudRenderEvent(Minecraft.getInstance().gui, graphics, float)
        }
    }
}
