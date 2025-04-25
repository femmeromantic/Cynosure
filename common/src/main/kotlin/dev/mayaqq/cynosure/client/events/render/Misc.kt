package dev.mayaqq.cynosure.client.events.render

import dev.mayaqq.cynosure.events.api.Event

/**
 * Invoked when the level renderer reloads
 */
public object ReloadLevelRendererEvent : Event

/**
 * Invoked when the game renderer resizes
 */
public class ResizeRendererEvent(public val width: Int, public val height: Int) : Event