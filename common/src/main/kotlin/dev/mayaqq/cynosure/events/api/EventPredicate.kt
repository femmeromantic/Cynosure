package dev.mayaqq.cynosure.events.api

import dev.mayaqq.cynosure.utils.loadService
import java.lang.reflect.Method

public typealias EventPredicate = (event: Event, context: Any?) -> Boolean

private val providers = loadService<EventPredicateProvider>().toList()

internal fun createEventPredicates(data: EventListenerData): List<EventPredicate> = providers.mapNotNull { it.getPredicate(data) }

internal fun List<EventPredicate>.test(event: Event, context: Any?): Boolean =
    all { it(event, context) }

public interface EventPredicateProvider {
    public fun getPredicate(method: EventListenerData): EventPredicate?
}

public data class EventListenerData(
    val type: Type,
    val className: String,
    val methodName: String?,
    val event: Class<out Event>
) {
    public constructor(method: Method) : this(
        Type.METHOD,
        method.declaringClass.canonicalName,
        method.name,
        method.parameterTypes[0] as Class<out Event>
    )

    public enum class Type {
        CALLBACK,
        METHOD,
        AUTO_METHOD
    }
}