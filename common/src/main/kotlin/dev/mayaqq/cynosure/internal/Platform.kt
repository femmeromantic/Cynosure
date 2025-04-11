package dev.mayaqq.cynosure.internal

internal inline fun <reified T : Any> loadPlatform(implName: String = ""): T = loadPlatform(T::class.java, implName)

@JvmOverloads
internal fun <T : Any> loadPlatform(clazz: Class<T>, implName: String = ""): T {
    val implClass = Class.forName(implName.ifEmpty { "${clazz.canonicalName}Impl" })
    if (!clazz.isAssignableFrom(implClass)) error("Impl class does not extend api class")
    val kt = implClass.kotlin
    return (kt.objectInstance ?: implClass.getConstructor().newInstance()) as T
}

