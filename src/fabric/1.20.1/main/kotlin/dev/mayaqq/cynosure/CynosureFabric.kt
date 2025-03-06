package dev.mayaqq.cynosure

import dev.mayaqq.cynosure.events.LateInitEvent
import dev.mayaqq.cynosure.events.api.post

public object CynosureFabric {
    public fun init() {
        Cynosure.init()
    }

    public fun lateinit() {
        LateInitEvent.post()
    }
}