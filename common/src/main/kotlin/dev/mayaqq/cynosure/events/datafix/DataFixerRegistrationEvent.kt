package dev.mayaqq.cynosure.events.datafix

import com.mojang.datafixers.DataFix
import com.mojang.datafixers.DataFixer
import com.mojang.datafixers.DataFixerBuilder
import com.mojang.datafixers.schemas.Schema
import dev.mayaqq.cynosure.events.api.Event

public class DataFixerRegistrationEvent(public val builder: DataFixerBuilder) : Event {

    public inline fun addSchema(version: Int, subversion: Int = 0, crossinline factory: (Int, Schema) -> Schema): Schema {
        return builder.addSchema(version, subversion) { t, u -> factory(t, u) }
    }

    public fun addSchema(schema: Schema) {
        builder.addSchema(schema)
    }

    public fun addFixer(fixer: DataFix) {
        builder.addFixer(fixer)
    }

    public fun addFixers(vararg fixers: DataFix) {
        fixers.forEach(builder::addFixer)
    }
}