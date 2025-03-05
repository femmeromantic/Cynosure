package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.CynosureFabric


public object CynosureClientFabric {
    public fun init() {
        CynosureFabric.lateinit()
        CynosureClient.init()
    }
}