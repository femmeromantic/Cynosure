package dev.mayaqq.cynosure.events.api

import dev.mayaqq.cynosure.Cynosure
import dev.mayaqq.cynosure.events.internal.generateASMEventListener
import java.lang.invoke.LambdaMetafactory
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Method
import java.util.function.Consumer

internal class EventListeners {

    private val listeners: MutableList<Listener> = mutableListOf()

    fun removeListener(listener: Any) {
        listeners.removeIf { it.listener == listener }
    }

    fun <T : Event> addListener(callback: (T) -> Unit, eventType: Class<T>, priority: Int, receiveCancelled: Boolean) {
        @Suppress("UNCHECKED_CAST")
        listeners.add(
            Listener(
            callback,
            { callback(it as T) },
            priority,
            receiveCancelled,
            createEventPredicates(EventListenerData(
                type = EventListenerData.Type.CALLBACK,
                className = callback.javaClass.name,
                methodName = null,
                event = eventType
            ))
        )
        )
    }

    fun addListener(method: Method, instance: Any?, options: Subscription) {
        val name = "${method.declaringClass.name}.${method.name}${
            method.parameterTypes.joinTo(
                StringBuilder(),
                prefix = "(",
                postfix = ")",
                separator = ", ",
                transform = Class<*>::getTypeName,
            )
        }"
        listeners.add(
            Listener(
            method,
            instance?.createEventConsumer(name, method) ?: createStaticEventConsumer(name, method),
            options.priority,
            options.receiveCancelled,
            createEventPredicates(EventListenerData(method))
        ))
    }

    fun addASMListener(className: String, methodName: String, methodDesc: String, instanceFieldName: String?, instanceFieldOwner: String?, eventType: Class<out Event>, priority: Int, receiveCancelled: Boolean) {
        try {
            listeners.add(
                Listener(
                    "$className;$methodName$methodDesc",
                    generateASMEventListener(className, methodName, methodDesc, instanceFieldName, instanceFieldOwner),
                    priority,
                    receiveCancelled,
                    createEventPredicates(EventListenerData(
                        type = EventListenerData.Type.AUTO_METHOD,
                        className = className.replace('/', '.'),
                        methodName = methodName,
                        event = eventType
                    ))
                )
            )
        } catch (ex: Exception) {
            Cynosure.error("Error registering asm event listener '$className;$methodName$methodDesc'", ex)
        }
    }

    /**
     * Creates a consumer using LambdaMetafactory, this is the most efficient way to reflectively call
     * a method from within code.
     */
    @Suppress("UNCHECKED_CAST")
    private fun Any.createEventConsumer(name: String, method: Method): Consumer<Any> {
        try {
            val handle = MethodHandles.lookup().unreflect(method)
            return LambdaMetafactory.metafactory(
                MethodHandles.lookup(),
                "accept",
                MethodType.methodType(Consumer::class.java, javaClass),
                MethodType.methodType(Nothing::class.javaPrimitiveType, Object::class.java),
                handle,
                MethodType.methodType(Nothing::class.javaPrimitiveType, method.parameterTypes[0]),
            ).target.bindTo(this).invokeExact() as Consumer<Any>
        } catch (e: Throwable) {
            throw IllegalArgumentException("Method $name is not a valid consumer", e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun createStaticEventConsumer(name: String, method: Method): Consumer<Any> {
        try {
            val handle = MethodHandles.lookup().unreflect(method)
            return LambdaMetafactory.metafactory(
                MethodHandles.lookup(),
                "accept",
                MethodType.methodType(Consumer::class.java),
                MethodType.methodType(Nothing::class.javaPrimitiveType, Object::class.java),
                handle,
                MethodType.methodType(Nothing::class.javaPrimitiveType, method.parameterTypes[0]),
            ).target.invokeExact() as Consumer<Any>
        } catch (e: Throwable) {
            throw IllegalArgumentException("Method $name is not a valid consumer", e)
        }
    }

    fun getListeners(): List<Listener> = listeners

    class Listener(
        val listener: Any,
        val invoker: Consumer<Any>,
        val priority: Int,
        val receiveCancelled: Boolean,
        val predicate: List<EventPredicate>,
    )
}