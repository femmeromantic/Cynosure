package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.client.events.render.EndRenderHudEvent
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.Minecraft

internal object CynosureClientFabricEvents {
    fun init() {
        HudRenderCallback.EVENT.register { graphics, float ->
            EndRenderHudEvent(Minecraft.getInstance().gui, graphics, float)
        }
    }
}
