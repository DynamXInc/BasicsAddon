plugins {
    id("fabric-loom") version "1.13.6"
}

// Versions alignées sur le build.gradle.kts de DynamX
val minecraftVersion = "1.21.11"
val loaderVersion = "0.18.2"
val fabricVersion = "0.139.4+1.21.11"
val physxJniVersion = "2.7.1"
val jacksonVersion = "2.18.3"
val lwjglVersion = "3.3.3"

// JAR de DynamX compilé localement (./gradlew build dans le projet DynamX).
// Par défaut : projet frère du dossier courant ; sinon -PdynamxJar=/chemin/vers/le.jar
val dynamxJar = providers.gradleProperty("dynamxJar")
    .getOrElse("../DynamX/build/libs/dynamx-0.1.0.jar")

version = "0.1.0"
group = "fr.dynamx.addon"

base {
    archivesName.set("dynamx-basic-addon")
}

repositories {
    mavenCentral()
}

dependencies {
    // Minecraft & Fabric
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

    // DynamX : API d'addon (DynamXAddon, DynamXApiContext, ECS, ...)
    modImplementation(files(dynamxJar))

    // Dépendances runtime de DynamX — elles sont en `implementation` chez lui et ne sont
    // pas embarquées dans son JAR, il faut donc les fournir pour lancer le client/serveur de dev.
    runtimeOnly("de.fabmax:physx-jni:$physxJniVersion")
    runtimeOnly("de.fabmax:physx-jni:$physxJniVersion:natives-windows")
    runtimeOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    runtimeOnly("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")
    runtimeOnly("org.lwjgl:lwjgl-assimp:$lwjglVersion")
    runtimeOnly("org.lwjgl:lwjgl-assimp:$lwjglVersion:natives-windows")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

loom {
    runs {
        configureEach {
            vmArg("-Xmx4G")
            vmArg("-XX:+UseG1GC")
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// Les sources sont en UTF-8 (messages français accentués) ; sans ça, javac lit en cp1252
// sous Windows et les accents sortent en mojibake dans le jeu.
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
