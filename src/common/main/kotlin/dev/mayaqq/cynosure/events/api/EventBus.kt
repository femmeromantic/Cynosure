package dev.mayaqq.cynosure.events.api

import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * Global event bus where all minecraft events are run
 */
public object GlobalBus : EventBus()

/**
 * Subscribe to an event bus. If the receiver is an instance or object any instance method marked with [Subscription]
 * will be added to the event bus. If the receiver is a [KClass] or [Class] instance, any static methods of the class
 * will be added to the event bus
 */
public fun Any.subscribeTo(bus: EventBus) {
    bus.addSubscriber(this)
}

/**
 * Abstract because all event buses must be object declarations
 */
public abstract class EventBus {

    private val listeners: MutableMap<Class<*>, EventListeners> = mutableMapOf()
    private val handlers: MutableMap<Class<*>, EventHandler<*>> = mutableMapOf()

    public inline fun <reified E : Event> register(priority: Int = 0, receiveCancelled: Boolean = false, noinline handler: (E) -> Unit) {
        register(E::class.java, priority, receiveCancelled, handler)
    }

    public fun <E : Event> register(clazz: Class<E>, priority: Int = 0, receiveCancelled: Boolean = false, handler: (E) -> Unit) {
        unregisterHandler(clazz)
        listeners.getOrPut(clazz) { EventListeners() }.addListener(handler, priority, receiveCancelled)
    }

    public inline fun <reified T : Event> unregister(noinline callback: (T) -> Unit) {
        unregister(T::class.java, callback = callback)
    }

    public fun <T : Event> unregister(type: Class<T>, callback: (T) -> Unit) {
        unregisterHandler(type)
        listeners.values.forEach { it.removeListener(callback) }
    }

    public fun post(
        event: Event,
        context: Any? = null,
        onError: ((Throwable) -> Unit)? = null
    ): Boolean = getHandler(event.javaClass).post(event, context, onError)

    @Suppress("UNCHECKED_CAST")
    private fun <T : Event> getHandler(event: Class<T>): EventHandler<T> = handlers.getOrPut(event) {
        EventHandler(
            event,
            getEventClasses(event).mapNotNull { listeners[it] }.flatMap(EventListeners::getListeners)
        )
    } as EventHandler<T>

    private fun unregisterHandler(clazz: Class<*>) = this.handlers.keys
        .filter { it.isAssignableFrom(clazz) }
        .forEach(this.handlers::remove)

    internal fun addSubscriber(instance: Any) {
        val thing = if(instance is KClass<*>) instance.java else instance
        if(thing is Class<*>) {
            thing.declaredMethods.filter { Modifier.isStatic(it.modifiers) }
                .forEach { registerMethod(it, null) }
        } else {
            javaClass.declaredMethods.filter { !Modifier.isStatic(it.modifiers) }
                .forEach { registerMethod(it, thing) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun registerMethod(method: Method, instance: Any? = null) {
        if (method.parameterCount != 1) return
        val options = method.getAnnotation(Subscription::class.java) ?: return

        val event = method.parameterTypes[0]
        if (!Event::class.java.isAssignableFrom(event)) return
        unregisterHandler(event)
        listeners.getOrPut(event as Class<Event>) { EventListeners() }.let {
            if(instance != null) it.addListener(method, instance, options)
        }
    }

    private fun getEventClasses(clazz: Class<*>): List<Class<*>> {
        val classes = mutableListOf<Class<*>>()
        classes.add(clazz)

        var current = clazz
        while (current.superclass != null) {
            val superClass = current.superclass
            if (superClass == Any::class.java
                || superClass.getAnnotation(RootEventClass::class.java) != null) break
            classes.add(superClass)
            current = superClass
        }
        return classes
    }
}