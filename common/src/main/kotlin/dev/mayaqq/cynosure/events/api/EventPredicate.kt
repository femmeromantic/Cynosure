package dev.mayaqq.cynosure.events.api

import java.lang.reflect.Method
import java.util.*

public typealias EventPredicate = (event: Event, context: Any?) -> Boolean

private val providers = ServiceLoader.load(EventPredicateProvider::class.java).toList()

internal class EventPredicates(private val predicates: List<EventPredicate>) {

    constructor(method: Method) : this(providers.mapNotNull { it.getPredicate(method) })

    fun test(event: Event, context: Any?): Boolean =
        predicates.all { it(event, context) }
}

public interface EventPredicateProvider {
    public fun getPredicate(method: Method): EventPredicate?
}