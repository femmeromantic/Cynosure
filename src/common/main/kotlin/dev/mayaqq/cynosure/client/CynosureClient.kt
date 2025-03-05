package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.events.LateInitEvent
import dev.mayaqq.cynosure.events.api.post

public object CynosureClient {
    public fun init() {
        LateInitEvent.post()
    }
}