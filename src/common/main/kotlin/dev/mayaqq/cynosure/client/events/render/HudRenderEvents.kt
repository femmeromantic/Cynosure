package dev.mayaqq.cynosure.client.events.render

import dev.mayaqq.cynosure.events.api.CancellableEvent
import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiGraphics

public class BeginRenderHudEvent(
    public val gui: Gui,
    public val graphics: GuiGraphics,
    public val partialTicks: Float
) : CancellableEvent()

public class EndRenderHudEvent(
    public val gui: Gui,
    public val graphics: GuiGraphics,
    public val partialTicks: Float
) : Event