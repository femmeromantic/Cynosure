package dev.mayaqq.cynosure.utils.asm

import org.objectweb.asm.tree.AnnotationNode

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