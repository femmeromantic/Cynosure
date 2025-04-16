package dev.mayaqq.cynosure.utils.asm

import org.objectweb.asm.tree.AnnotationNode
import java.io.ByteArrayOutputStream

public val AnnotationNode.mappedValues: Map<String, Any?>
    get() {
        val map: MutableMap<String, Any?> = mutableMapOf()
        values?.forEachIndexed { index, value ->
            if (index % 2 == 0) map[value as String] = values[index + 1]
        }
        return map
    }

public fun String.descriptorToClassName(): String = substringAfter('L')
    .substringBefore(';')
    .replace('/', '.')

public fun ClassLoader.getClassByteArray(className: String): ByteArray? {
    return getResource(classFileFromName(className))?.openStream()?.use { inputStream ->
        val a: Int = inputStream.available()
        val outputStream = ByteArrayOutputStream(if (a < 32) 32768 else a)
        val buffer = ByteArray(8192)
        var len: Int

        while ((inputStream.read(buffer).also { len = it }) > 0) {
            outputStream.write(buffer, 0, len)
        }
        outputStream.toByteArray()
    }
}

public fun classFileFromName(name: String): String = name.replace('.', '/') + ".class"