package dev.mayaqq.cynosure

import dev.mayaqq.cynosure.events.api.AutoSubscriber
import dev.mayaqq.cynosure.events.api.EventBus
import dev.mayaqq.cynosure.events.api.GlobalBus
import dev.mayaqq.cynosure.events.api.subscribeTo
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.impl.launch.FabricLauncherBase
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.extension
import kotlin.io.path.walk

private const val AUTOSUB_KEY = "cynosure:autosubscriptions"

private val AUTOSUB_ANNOTATION = AutoSubscriber::class.qualifiedName!!

private fun String.descriptorToClassName(): String = substringAfter('L')
    .substringBefore(';')
    .replace('/', '.')

@OptIn(ExperimentalPathApi::class)
public fun onPreLaunch() {
    for(mod in FabricLoader.getInstance().allMods) {
        if(!(mod.metadata.containsCustomValue(AUTOSUB_KEY)
                    && mod.metadata.getCustomValue(AUTOSUB_KEY).asBoolean)) continue

        val classPaths = mod.rootPaths.asSequence().flatMap { path ->
            path.walk()
                .filter { it.extension == "class" }
                .map { path.relativize(it) }
        }

        for(classPath in classPaths) {
            val className = classPath.joinToString(".").dropLast(6)
            val reader = ClassReader(FabricLauncherBase.getLauncher().getClassByteArray(className, false))
            val cn = ClassNode()

            // We are only interested in annotations on methods
            reader.accept(cn, ClassReader.SKIP_CODE or ClassReader.SKIP_FRAMES)

            val annotation = cn.visibleAnnotations
                .find { it.desc.descriptorToClassName() == AUTOSUB_ANNOTATION }
                ?: continue

            try {
                val bus =
                    (annotation.values?.get(1) as? Type)?.let { Class.forName(it.className).kotlin.objectInstance as EventBus }
                        ?: GlobalBus

                val clazz = Class.forName(className)
                // If the class is an object, subscribe the object instance instead of the class
                clazz.kotlin.objectInstance?.subscribeTo(bus) ?: clazz.subscribeTo(bus)

            } catch (e: ReflectiveOperationException) {
                continue
            }
        }

    }
}
