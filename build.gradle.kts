import com.sun.tools.attach.spi.AttachProvider.providers

plugins {
    alias(libs.plugins.loom)
    alias(libs.plugins.maven.publish)
}
val buildNum = providers.environmentVariable("GITHUB_RUN_NUMBER")
    .filter {!("$it".isEmpty())}
    .map {"-build.$it"}
    .orElse("-local")
    .getOrElse("")

val mcVer: String = "+mc${libs.versions.minecraft.get()}"
val modVer: String = project.property("mod_version") as String

version = "$modVer$mcVer$buildNum"
group = project.property("maven_group") as String

base {
    archivesName = project.property("archives_base_name") as String
}

repositories {
    mavenCentral()
    maven("https://api.modrinth.com/maven")
    maven("https://maven.terraformersmc.com/")
}

// All the dependencies are declared at gradle/libs.version.toml and referenced with "libs.<id>"
// See https://docs.gradle.org/current/userguide/platforms.html for information on how version catalogs work.
dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric.loader)

    modImplementation(libs.bundles.dependencies)
    modLocalRuntime(libs.bundles.dev)
}

tasks.processResources {
    val properties: Map<String, Any> = mapOf(
        "version" to project.version,
        "minecraft_version" to libs.versions.minecraft.get(),
        "loader_version" to libs.versions.fabric.loader.get()
    )

    inputs.properties(properties)

    filesMatching("fabric.mod.json") {
        expand(properties)
    }
}

java {
    val javaVersion = JavaVersion.toVersion(21)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}"}
    }
}

publishing {
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
            }
        }

        repositories {
            listOf("Releases", "Snapshots").forEach {
                maven("https://mvn.devos.one/${it.lowercase()}") {
                    name = "devOS$it"
                    credentials(PasswordCredentials::class)
                }
            }
        }
    }
}