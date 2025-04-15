package dev.mayaqq.cynosure.utils

import dev.mayaqq.cynosure.utils.function.and
import dev.mayaqq.cynosure.utils.unsafe.*
import java.lang.invoke.MethodHandles
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.properties.ReadOnlyProperty

public inline fun <reified E : Enum<E>> createEnumConstant(id: String, builder: EnumBuilder<E>.() -> Unit = {}): E {
    val b = EnumBuilder(E::class.java, id)
    b.builder()
    return b.build()
}

public inline fun <reified E : Enum<E>> enumConstant(crossinline builder: EnumBuilder<E>.() -> Unit = {}): ReadOnlyProperty<Any?, E> {
    return ReadOnlyProperty { _, property ->
        createEnumConstant(property.name, builder)
    }
}

private typealias Predicate<T> = (T) -> Boolean

@Suppress("unused")
public class EnumBuilder<T : Enum<T>>(private val enumClass: Class<T>, private val id: String) {
    private val args: MutableList<Any> = ArrayList()
    private val types: MutableList<Class<*>> = ArrayList()

    public fun arg(type: Class<*>, arg: Any): EnumBuilder<T> {
        args.add(arg)
        types.add(type)
        return this
    }

    public inline fun <reified A : Any> arg(arg: A): EnumBuilder<T> {
        return arg(A::class.java, arg)
    }

    @Throws(Throwable::class)
    public fun build(): T {
        val argTypes: Array<Class<*>?> = arrayOfNulls(types.size + 2)
        argTypes[0] = String::class.java
        argTypes[1] = Int::class.javaPrimitiveType!!
        System.arraycopy(types.toTypedArray(), 0, argTypes, 2, types.size)

        val newArgs = arrayOfNulls<Any>(args.size + 2)
        newArgs[0] = id
        newArgs[1] = enumClass.enumConstants.size
        System.arraycopy(args.toTypedArray(), 0, newArgs, 2, args.size)

        verify(argTypes, newArgs)

        return create(enumClass, newArgs, argTypes)
    }

    private fun verify(argTypes: Array<Class<*>?>, newArgs: Array<Any?>) {
        require(argTypes.size == newArgs.size) { "Argument types and arguments must be the same length" }
    }

    private companion object {
        private val ENUM_CONSTANTS: Field =
            getField<Class<*>> { field: Field -> field.name == "enumConstants" }
        private val ENUM_CONSTANT_DIRECTORY: Field =
            getField<Class<*>> { field: Field -> field.name == "enumConstantDirectory" }

        private val IS_SYNTHETIC: Predicate<Field> = Field::isSynthetic
        private val IS_ARRAY: Predicate<Field> = { field -> field.type.isArray }
        private val IS_STATIC: Predicate<Field> = { field -> Modifier.isStatic(field.modifiers) }
        private val NAMED_VALUES: Predicate<Field> = { field -> field.name.contains("VALUES") }
        private val IS_VALUES_FIELD: Predicate<Field> = IS_SYNTHETIC and IS_STATIC and IS_ARRAY and NAMED_VALUES

        //region Unsafe operations for enum creation
        @OptIn(CynosureUnsafeApi::class)
        @Throws(Throwable::class)
        private fun <T : Enum<T>> create(enumClass: Class<T>, args: Array<Any?>, types: Array<Class<*>?>): T {
            val constructor: Constructor<T> = enumClass.getDeclaredConstructor(*types)
            constructor.setAccessible(true)
            val output = MethodHandles.lookup()
                .unreflectConstructor(constructor)
                .invokeWithArguments(*args) as T

            val valuesField: Field = enumClass.getDeclaredField { field: Field ->
                IS_VALUES_FIELD(
                    field
                ) && field.type.componentType == enumClass
            }
            addArrayValue(valuesField, enumClass, output)
            setFieldUnsafe(enumClass, ENUM_CONSTANT_DIRECTORY, null)
            setFieldUnsafe(enumClass, ENUM_CONSTANTS, null)
            return output
        }

        @OptIn(CynosureUnsafeApi::class)
        private fun <T> addArrayValue(data: Field?, `object`: Class<T>, arrayEntry: T) {
            val base = UNSAFE.staticFieldBase(data)
            val offset = UNSAFE.staticFieldOffset(data)
            val array = UNSAFE.getObject(base, offset) as Array<T>
            val newArray = java.lang.reflect.Array.newInstance(`object`, array.size + 1) as Array<T>
            System.arraycopy(array, 0, newArray, 0, array.size)
            newArray[array.size] = arrayEntry
            UNSAFE.putObject(base, offset, newArray)
        } //endregion
    }
}