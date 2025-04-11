enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "cynosure"

pluginManagement {
    repositories {
        mavenLocal()

        gradlePluginPortal()

        //maven(url = "https://maven.msrandom.net/repository/cloche")
        maven(url = "https://maven.fabricmc.net/")
        maven(url = "https://maven.minecraftforge.net/")
        maven(url = "https://repo.spongepowered.org/repository/maven-public/")
    }
}

dependencyResolutionManagement {
    versionCatalogs.create("libs") {
        from(files("libs.versions.toml"))
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}


include("common", "fabric", "forge")