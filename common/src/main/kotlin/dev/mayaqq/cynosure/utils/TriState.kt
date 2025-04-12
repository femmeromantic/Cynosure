package dev.mayaqq.cynosure.utils

public enum class TriState {
    TRUE,
    INTERMEDIATE,
    FALSE;

    public fun toBoolean(): Boolean = this == TRUE

    public operator fun not(): TriState = when (this) {
        TRUE -> FALSE
        FALSE -> TRUE
        else -> this
    }
}