package dev.mayaqq.cynosure.utils

public enum class TriState {
    TRUE,
    INTERMEDIATE,
    FALSE;

    public fun toBoolean(): Boolean = this == TRUE

    public fun toNullableBoolean(): Boolean? = when (this) {
        TRUE -> true
        FALSE -> false
        else -> null
    }

    public operator fun not(): TriState = when (this) {
        TRUE -> FALSE
        FALSE -> TRUE
        else -> this
    }
}

@JvmName("toTriStateNullable")
public fun Boolean?.toTriState(): TriState = when (this) {
    true -> TriState.TRUE
    false -> TriState.FALSE
    null -> TriState.INTERMEDIATE
}


public fun Boolean.toTriState(): TriState = if (this) TriState.TRUE else TriState.FALSE
