package dev.mayaqq.cynosure.events.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.ParseResults
import dev.mayaqq.cynosure.events.api.CancellableEvent
import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

/**
 * Fired right before a command executes, cancelling this event prevents command execution
 *
 * @property parseResults allows changing the command's parse results
 * @property exception exception to be thrown if the event is cancelled
 */
public class CommandExecuteEvent(
    public var parseResults: ParseResults<CommandSourceStack>,
    public var exception: Throwable?
) : CancellableEvent()

/**
 * Fired during command registration, allows you to register custom commands
 */
public class CommandRegistrationEvent(
    public val dispatcher: CommandDispatcher<CommandSourceStack>,
    public val context: CommandBuildContext,
    public val selection: Commands.CommandSelection
) : Event