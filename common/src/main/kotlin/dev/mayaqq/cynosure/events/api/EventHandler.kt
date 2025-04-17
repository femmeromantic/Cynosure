package dev.mayaqq.cynosure.events.api

internal class EventHandler<T : Event> private constructor(
    val name: String,
    private val listeners: List<EventListeners.Listener>,
    private val canReceiveCancelled: Boolean,
) {

    constructor(event: Class<T>, listeners: List<EventListeners.Listener>) : this(
        (event.name.split(".").lastOrNull() ?: event.name).replace("$", "."),
        listeners.sortedBy { it.priority }.toList(),
        listeners.any { it.receiveCancelled }
    )

    fun post(event: T, context: Any?, onError: ((Throwable) -> Unit)? = null): Boolean {
        if (this.listeners.isEmpty()) return false

        for (listener in listeners) {
            if (!listener.predicate.test(event, context)) continue
            if (event.isCancelled && !listener.receiveCancelled) continue
            try {
                listener.invoker.accept(event)
            } catch (throwable: Throwable) {
                onError?.invoke(throwable)
                    ?: throw RuntimeException("Exceptione while handling event $event", throwable)
            }
            if (event.isCancelled && !canReceiveCancelled) break
        }
        return event.isCancelled
    }

}