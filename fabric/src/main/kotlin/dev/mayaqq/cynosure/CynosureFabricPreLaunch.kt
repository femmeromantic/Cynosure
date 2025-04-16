package dev.mayaqq.cynosure

import dev.mayaqq.cynosure.events.api.*
import dev.mayaqq.cynosure.events.internal.CynosureEventLogger
import dev.mayaqq.cynosure.events.internal.subscribeASMMethods
import dev.mayaqq.cynosure.internal.boolean
import dev.mayaqq.cynosure.internal.getCynosureValue
import dev.mayaqq.cynosure.utils.Environment
import dev.mayaqq.cynosure.utils.PlatformHooks
import dev.mayaqq.cynosure.utils.asm.descriptorToClassName
import dev.mayaqq.cynosure.utils.asm.mappedValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.impl.launch.FabricLauncherBase
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.extension
import kotlin.io.path.walk

private const val AUTOSUB_KEY = "autosubscription"

private val AUTOSUB_ANNOTATION = EventSubscriber::class.qualifiedName!!

@CynosureInternal
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPathApi::class)
internal fun onPreLaunch() {
    val flow = FabricLoader.getInstance()
        .allMods
        .asFlow()
        .filter {
            it.metadata.getCynosureValue(AUTOSUB_KEY).boolean
        }
        .flatMapMerge(16) { mod: ModContainer ->
            mod.rootPaths.asFlow().flatMapMerge(16) { path ->
                path.walk().asFlow()
                    .filter { it.extension == "class" }
                    .map { mod to path.relativize(it) }
            }
        }
        .onEach { (mod, classPath) ->
            val className = classPath.joinToString(".").dropLast(6)
            val reader = ClassReader(FabricLauncherBase.getLauncher().getClassByteArray(className, false))
            val cn = ClassNode()

            reader.accept(cn, ClassReader.SKIP_FRAMES)

            val annotation = cn.visibleAnnotations
                ?.find { it.desc.descriptorToClassName() == AUTOSUB_ANNOTATION }
                ?: return@onEach

            val subscriberData = annotation.mappedValues

            // TODO: Sided event handlers
            //val env = subscriberData["env"]
            //if (env != null && env != PlatformHooks.environment) return@onEach

            try {
                val bus =
                    (subscriberData["bus"] as? Type)?.let {
                        Class.forName(it.className).let {
                            (it.kotlin.objectInstance ?: it.getConstructor(String::class.java).newInstance(mod.metadata.id)) as? EventBus
                        }
                    } ?: MainBus

                subscribeASMMethods(bus, cn)

                if (FabricLoader.getInstance().isDevelopmentEnvironment || System.getProperty("cynosure.logEventSubscribers")?.toBoolean() == true)
                    CynosureEventLogger.info("Registered cynosure event subscriber for mod ${mod.metadata.id}: $className to bus $bus")

            } catch (e: ReflectiveOperationException) {
                CynosureEventLogger.error("Failed to register event bus subscriber for mod ${mod.metadata.id}", e)
            }
        }

    runBlocking(Dispatchers.Default) {
        launch { flow.collect() }
    }

}
