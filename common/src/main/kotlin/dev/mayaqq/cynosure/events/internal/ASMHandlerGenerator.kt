package dev.mayaqq.cynosure.events.internal

import codes.som.koffee.assembleClass
import codes.som.koffee.insns.jvm.*
import codes.som.koffee.modifiers.final
import codes.som.koffee.modifiers.public
import codes.som.koffee.sugar.ClassAssemblyExtension.init
import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.events.api.EventBus
import dev.mayaqq.cynosure.events.api.Subscription
import dev.mayaqq.cynosure.utils.asm.descriptorToClassName
import dev.mayaqq.cynosure.utils.asm.mappedValues
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes.V17
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.function.Consumer
import kotlin.metadata.ClassKind
import kotlin.metadata.jvm.KotlinClassMetadata
import kotlin.metadata.jvm.Metadata
import kotlin.metadata.kind

private val METADATA: String = Metadata::class.qualifiedName!!

private val SUBSCRIPTION: String = Subscription::class.qualifiedName!!

@CynosureInternal
public val CynosureEventLogger: Logger = LoggerFactory.getLogger("Cynosure Event Registration")

@OptIn(CynosureInternal::class)
@Suppress("UNCHECKED_CAST")
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
                getstatic(instanceFieldOwnerName, instanceFieldName, className)
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

    val metadata = classNode.visibleAnnotations
        ?.find { it.desc.descriptorToClassName() == METADATA }
        ?.mappedValues
        ?.let {
            Metadata(
                kind = it["k"] as? Int,
                metadataVersion = (it["mv"] as? List<Int>)?.toIntArray(),
                data1 = (it["d1"] as? List<String>)?.toTypedArray(),
                data2 = (it["d2"] as? List<String>)?.toTypedArray(),
                extraString = it["xs"] as? String,
                packageName = it["pn"] as? String,
                extraInt = it["xi"] as? Int
            )
        }
        ?.let(KotlinClassMetadata::readLenient)

    val instanceFieldName: String?
    val instanceFieldOwner: String?

    if (metadata is KotlinClassMetadata.Class) {
        val klass = metadata.kmClass
        when (klass.kind) {
            ClassKind.OBJECT -> {
                instanceFieldOwner = classNode.name
                instanceFieldName = "INSTANCE"
            }
            ClassKind.COMPANION_OBJECT -> {
                instanceFieldOwner = classNode.name.substringBeforeLast('\$')
                instanceFieldName = classNode.name.substringAfterLast('\$')
            }
            else ->  {
                instanceFieldOwner = null
                instanceFieldName = null
            }
        }
    } else {
        instanceFieldName = null
        instanceFieldOwner = null
    }

    for (method in classNode.methods) {

        if (method.parameters != null && method.parameters.size != 1) continue

        val options = method.visibleAnnotations
            ?.find { it.desc.descriptorToClassName() == SUBSCRIPTION }
            ?.mappedValues
            ?: continue

        bus.registerASMMethod(classNode.name, method.name, method.desc, options, instanceFieldName, instanceFieldOwner)
    }
}