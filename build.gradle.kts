plugins {
    id("earth.terrarium.cloche") version "0.7.5"
}


repositories {
    mavenLocal()

    mavenCentral()

    maven("https://maven.msrandom.net/repository/root")
}

cloche {
    metadata {
        modId.set("cynosure")
    }

    val fabricCommon = common("fabric:common") {
        dependencies {
            fabricApi("0.92.2+1.20.1")
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

        dependsOn(fabricCommon)
    }

    fabric("fabric:1.21.1") {
        loaderVersion.set("0.16.9")
        minecraftVersion.set("1.21.1")

        client()
        server()

        dependencies {
            fabricApi("0.110.0+1.21.1")
        }

        dependsOn(fabricCommon)
    }

    forge("forge:1.20.1") {
        loaderVersion.set("47.1.3")
        minecraftVersion.set("1.20.1")

        client()
        server()
    }

    neoforge("neoforge:1.21.1") {
        loaderVersion.set("21.1.90")
        minecraftVersion.set("1.21.1")

        client()
        server()
    }
}