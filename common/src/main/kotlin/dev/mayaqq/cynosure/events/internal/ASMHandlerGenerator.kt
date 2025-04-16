package dev.mayaqq.cynosure.events.internal

import codes.som.koffee.assembleClass
import codes.som.koffee.insns.jvm.*
import codes.som.koffee.modifiers.final
import codes.som.koffee.modifiers.public
import codes.som.koffee.sugar.ClassAssemblyExtension.init
import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.events.api.EventBus
import dev.mayaqq.cynosure.utils.PlatformHooks
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes.V17
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.LambdaMetafactory
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.function.Consumer

@CynosureInternal
public val CynosureEventLogger: Logger = LoggerFactory.getLogger("Cynosure Event Registration")

@OptIn(CynosureInternal::class)
internal fun generateASMEventListener(className: String, methodName: String, methodDesc: String, instanceFieldName: String?, instanceFieldOwnerName: String?): Consumer<Any> {
    val event = Type.getType(methodDesc.substringAfter('(').substringBefore(')'))

    val instanceFieldOwnerName = instanceFieldOwnerName ?: className

    val handler = assembleClass(
        public + final,
        "dev/mayaqq/cynosure/events/internal/${className.replace('/', '_')}\$EventListener\$$methodName\$${event.hashCode()}",
        version = V17,
        interfaces = listOf(Consumer::class)
    ) {

        init(public) {
            // super call is implicit
            _return
        }

        method(public, "accept", void, Any::class) {
            if (instanceFieldName == null) {
                aload_1
                checkcast(event)
                invokestatic(className, methodName, methodDesc)
            } else {
                getstatic(className, instanceFieldName, instanceFieldOwnerName)
                aload_1
                checkcast(event)
                invokevirtual(className, methodName, methodDesc)
            }
            _return
        }
    }

    val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES)
    handler.accept(cw)
    val lookup = MethodHandles.lookup().defineHiddenClass(cw.toByteArray(), true)
    val ctor = lookup.findConstructor(lookup.lookupClass(), MethodType.methodType(Nothing::class.javaPrimitiveType))
    return ctor.invoke() as Consumer<Any>
}

@CynosureInternal
public fun subscribeASMMethods(bus: EventBus, classNode: ClassNode) {
    for (method in classNode.methods) {
        bus.registerASMMethod(classNode, method)
    }
}