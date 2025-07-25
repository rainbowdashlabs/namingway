rootProject.name = "namingway"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // misc
            library("jetbrains-annotations", "org.jetbrains:annotations:26.0.2")

            version("log4j", "2.25.1")

            library("slf4j-api", "org.slf4j:slf4j-api:2.0.17")
            library("log4j-core", "org.apache.logging.log4j", "log4j-core").versionRef("log4j")
            library("log4j-slf4j2", "org.apache.logging.log4j", "log4j-slf4j2-impl").versionRef("log4j")
            library("log4j-jsontemplate","org.apache.logging.log4j", "log4j-layout-template-json").versionRef("log4j")
            bundle("log4j", listOf("slf4j-api", "log4j-core", "log4j-slf4j2", "log4j-jsontemplate"))

            // plugins
            plugin("spotless", "com.diffplug.spotless").version("7.2.1")
            plugin("shadow", "com.github.johnrengelman.shadow").version("8.1.1")

        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("1.0.0")
}
