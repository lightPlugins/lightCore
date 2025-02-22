plugins {
    java
    kotlin("jvm") version "1.8.0" // Ensure you have the Kotlin plugin applied
    id("io.freefair.lombok") version "8.11"
    id("com.gradleup.shadow") version "8.3.5"
    id("maven-publish")
}

group = "io.lightstudios.core"
version = "0.4.5"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }

    maven {
        name = "codemc"
        url = uri("https://repo.codemc.org/repository/maven-public")
    }

    maven {
        name = "placeholderapi"
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    maven {
        name = "worldguard"
        url = uri("https://maven.enginehub.org/repo/")
    }

    maven {
        name = "towny"
        url = uri("https://repo.glaremasters.me/repository/towny/")
    }

    maven {
        name = "nexo"
        url = uri("https://repo.nexomc.com/releases")
    }

    maven {
        name = "fancyholograms"
        url = uri("https://repo.fancyplugins.de/releases")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
    compileOnly("net.milkbowl.vault:VaultUnlockedAPI:2.9")
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    compileOnly("de.oliver:FancyHolograms:2.4.1")
    compileOnly("com.github.lightPlugins:lightCoins:0.0.2")
    compileOnly("org.mariadb.jdbc:mariadb-java-client:3.5.0")
    implementation("redis.clients:jedis:5.2.0")
    implementation("com.zaxxer:HikariCP:6.0.0")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.17")
    implementation("commons-lang:commons-lang:2.6")
    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("org.bstats:bstats-velocity:3.0.2")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.13-SNAPSHOT")
    compileOnly("com.palmergames.bukkit.towny:towny:0.100.2.0")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.nexomc:nexo:0.7.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    processResources {
        from(sourceSets.main.get().resources.srcDirs()) {
            filesMatching("plugin.yml") {
                expand(
                    "name" to rootProject.name,
                    "version" to rootProject.version
                )

            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("com.zaxxer.hikari", "io.lightstudios.core.util.libs.hikari")
        relocate("redis.clients.jedis", "io.lightstudios.core.util.libs.jedis")
        relocate("com.github.stefvanschie.inventoryframework", "io.lightstudios.core.util.libs.inv")
        relocate("org.bstats", "io.lightstudios.core.util.libs.bstats")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.shadowJar.get()) {
                classifier = null
            }
            groupId = "com.github.lightPlugins"
            artifactId = "lightCore"
            version = rootProject.version.toString()
        }
    }
}

tasks.named("publishMavenPublicationToMavenLocal") {
    dependsOn(tasks.shadowJar)
    dependsOn(tasks.jar)
}