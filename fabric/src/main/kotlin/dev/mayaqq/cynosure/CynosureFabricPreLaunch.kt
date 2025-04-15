package dev.mayaqq.cynosure

import dev.mayaqq.cynosure.events.api.EventBus
import dev.mayaqq.cynosure.events.api.EventSubscriber
import dev.mayaqq.cynosure.events.api.MainBus
import dev.mayaqq.cynosure.events.api.subscribeTo
import dev.mayaqq.cynosure.internal.boolean
import dev.mayaqq.cynosure.internal.getCynosureValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.impl.launch.FabricLauncherBase
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.extension
import kotlin.io.path.walk

private const val AUTOSUB_KEY = "autosubscriptions"

private val AUTOSUB_ANNOTATION = EventSubscriber::class.qualifiedName!!

private fun String.descriptorToClassName(): String = substringAfter('L')
    .substringBefore(';')
    .replace('/', '.')

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPathApi::class)
internal fun onPreLaunch() {
    val flow = FabricLoader.getInstance()
        .allMods
        .asFlow()
        .filter {
            it.metadata.getCynosureValue(AUTOSUB_KEY).boolean
        }
        .flatMapMerge(64) {
            it.rootPaths.asFlow().flatMapMerge(64) { path ->
                path.walk().asFlow()
                    .filter { it.extension == "class" }
                    .map { path.relativize(it) }
            }
        }
        .onEach { classPath ->
            val className = classPath.joinToString(".").dropLast(6)
            val reader = ClassReader(FabricLauncherBase.getLauncher().getClassByteArray(className, false))
            val cn = ClassNode()

            // We are only interested in annotations on methods
            reader.accept(cn, ClassReader.SKIP_CODE or ClassReader.SKIP_FRAMES)

            val annotation = cn.visibleAnnotations
                ?.find { it.desc.descriptorToClassName() == AUTOSUB_ANNOTATION }
                ?: return@onEach

            try {
                val bus =
                    (annotation.values?.get(1) as? Type)?.let { Class.forName(it.className).kotlin.objectInstance as? EventBus }
                        ?: MainBus

                val clazz = Class.forName(className)
                // If the class is an object, subscribe the object instance instead of the class
                clazz.kotlin.objectInstance?.subscribeTo(bus) ?: clazz.subscribeTo(bus)

            } catch (e: ReflectiveOperationException) {

            }
        }

    runBlocking(Dispatchers.Default) {
        launch { flow.collect() }
    }

}

