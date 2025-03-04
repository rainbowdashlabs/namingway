import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    java
}

group = "de.chojo"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://eldonexus.de/repository/maven-public")
    maven("https://eldonexus.de/repository/maven-proxies")
}

dependencies {

        //discord
    implementation("de.chojo", "cjda-util", "2.10.3+jda-5.1.0") {
        exclude(group = "club.minnced", module = "opus-java")
    }

        // Logging
    implementation(libs.bundles.log4j)
    implementation("de.chojo", "log-util", "1.0.1") {
        exclude("org.apache.logging.log4j")
    }

    implementation("dev.chojo", "ocular", "1.0.1")

    testImplementation(platform("org.junit:junit-bom:5.12.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
    withJavadocJar()
}
tasks {
    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("version") {
                expand(
                        "version" to project.version
                )
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    javadoc {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    shadowJar {
        transform(Log4j2PluginsCacheFileTransformer::class.java)
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "de.chojo.namingway.Namingway"))
        }
    }

    build {
        dependsOn(shadowJar)
    }
}
