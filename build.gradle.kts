import earth.terrarium.cloche.metadata.ModMetadata
import earth.terrarium.cloche.target.FabricTarget
import earth.terrarium.cloche.target.ForgeTarget
import earth.terrarium.cloche.target.MinecraftTarget

plugins {
    id("earth.terrarium.cloche") version "0.7.10"
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
        loaderVersion.set("0.16.9")
        minecraftVersion.set("1.20.1")

        client()
        server()

        dependencies {
            fabricApi("0.92.2+1.20.1")
        }
    }

    forge("forge:1.20.1") {
        loaderVersion.set("47.1.3")
        minecraftVersion.set("1.20.1")

        client()
        server()
    }

    targets.withType<ForgeTarget> {
        client {
            runConfiguration {
                arguments("--username", "Mayaqq", "--uuid", "a1732122-e22e-4edf-883c-09673eb55de8")
            }
        }
    }

    targets.withType<FabricTarget> {
        client {
            runConfiguration {
                arguments("--username", "Mayaqq", "--uuid", "a1732122-e22e-4edf-883c-09673eb55de8")
            }
        }
    }
}