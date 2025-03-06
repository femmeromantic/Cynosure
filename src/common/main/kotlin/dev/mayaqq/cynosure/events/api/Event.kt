package dev.mayaqq.cynosure.events.api

/**
 * Base interface for all events
 * [isCancelled] returns true if the event should be considered cancelled and stop execution
 */
public interface Event {
    public val isCancelled: Boolean get() = false
}

/**
 * Annotation marking an event class as a base class. This prevents people from subscribing to events of this class directly
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
public annotation class RootEventClass

/**
 * Event base class with a simple cancelling implementation
 */
@RootEventClass
public abstract class CancellableEvent : Event {

    private var cancelled: Boolean = false

    override val isCancelled: Boolean
        get() = cancelled

    public fun cancel() {
        cancelled = true
    }
}

/**
 * Event base class with a simple result implementation
 */
@RootEventClass
public abstract class ReturningEvent<R> : Event {
    public var result: R? = null

    override val isCancelled: Boolean
        get() = result !== null
}

/**
 * Post this event to the event bus, defaults to the [MainBus]
 */
public fun Event.post(bus: EventBus = MainBus) {
    bus.post(this)
}

/**
 * Post this returning event to the event bus and get its result. Defaults to [MainBus]
 */
public fun <R> ReturningEvent<R>.post(bus: EventBus = MainBus): R? {
    bus.post(this)
    return result
}
