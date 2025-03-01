plugins {
    kotlin("jvm") version "2.1.0"
    id("earth.terrarium.cloche") version "0.7.24"
}


repositories {
    mavenLocal()

    mavenCentral()

    maven("https://maven.msrandom.net/repository/root") // Most
}

cloche {
    metadata {
        modId.set("cynosure")
        name.set("Cynosure")
        description.set("Militech's answer to Arasaka's Soulkiller.")
        license.set("LGPL-3.0")
        icon.set("assets/cynosure/icon.png")
        url.set("https://github.com/MayaqqDev/Cynosure")
        sources.set("https://github.com/MayaqqDev/Cynosure")
    }

    cloche.common {
        mixins.from(file("src/common/main/cynosure.mixins.json"))

        client {
            mixins.from(file("src/common/client/cynosure-client.mixins.json"))
        }

        dependencies {
            compileOnly("org.spongepowered:mixin:0.8.3")
        }
    }

    fabric("fabric:1.20.1") {
        loaderVersion.set("0.16.10")
        minecraftVersion.set("1.20.1")

        client()
        server()

        dependencies {
            fabricApi("0.92.3+1.20.1")
        }
    }

    forge("forge:1.20.1") {
        loaderVersion.set("47.3.33")
        minecraftVersion.set("1.20.1")

        client()
        server()
    }

    fabric("fabric:1.21.1") {
        loaderVersion.set("0.16.10")
        minecraftVersion.set("1.21.1")

        client()
        server()

        dependencies {
            fabricApi("0.115.1+1.21.1")
        }
    }

    neoforge("neoforge:1.21.1") {
        loaderVersion.set("21.1.127")
        minecraftVersion.set("1.21.1")

        client()
        server()
    }
}