import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("kapt") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("edu.sc.seis.launch4j") version "2.5.1"
    id("de.inetsoftware.setupbuilder") version "7.2.13"
    id("java")
}

group = "toxic.games"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test"))

    val ktxVersion = "1.9.12-b1"
    val gdxVersion = "1.9.12"
    val exposedVersion = "0.34.1"
    val moshiVersion = "1.12.0"
    val retrofitVersion = "2.9.0"

    api(group = "com.badlogicgames.gdx", name = "gdx-backend-lwjgl", version = gdxVersion)
    api(group = "com.badlogicgames.gdx", name = "gdx-platform", version = gdxVersion, classifier = "natives-desktop")
    api(group = "com.badlogicgames.gdx", name = "gdx", version = gdxVersion)

    api(group = "io.github.libktx", name = "ktx-app", version = ktxVersion)
    api(group = "io.github.libktx", name = "ktx-collections", version = ktxVersion)
    api(group = "io.github.libktx", name = "ktx-graphics", version = ktxVersion)
    api(group = "io.github.libktx", name = "ktx-log", version = ktxVersion)
    api(group = "io.github.libktx", name = "ktx-actors", version = ktxVersion)
    api(group = "io.github.libktx", name = "ktx-scene2d", version = ktxVersion)
    api(group = "io.github.libktx", name = "ktx-style", version = ktxVersion)


    implementation("io.github.microutils:kotlin-logging-jvm:2.0.8")
    implementation("com.github.kamranzafar:jddl:b859f01358")

    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:4.2.1")
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    implementation("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:3.30.1")

    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("org.slf4j:slf4j-simple:1.6.1")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.jar {
    manifest {
        attributes(mapOf("Main-Class" to "com.bubul.col.launcher.MainKt"))
    }
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("CardsOfLegendsLauncher-Shadow")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "com.bubul.col.launcher.MainKt"))
        }
    }
}

launch4j {
    mainClassName = "com.bubul.col.launcher.MainKt"
    dontWrapJar = true
    jarTask = project.tasks.jar.get()
}

tasks.register<DefaultTask>("createPackage") {
    dependsOn("createExe")
    project.mkdir(layout.buildDirectory.dir("package"))
    val launcherDir = project.mkdir(layout.buildDirectory.dir("package/launcher"))
    val gameDir = project.mkdir(layout.buildDirectory.dir("package/game/resources"))
    project.copy {
        from(layout.buildDirectory.dir("launch4j"))
        into(launcherDir)
    }
    project.copy {
        from(layout.buildDirectory.dir("../resources"))
        into(gameDir)
    }
}

tasks.register<DefaultTask>("cleanPackage") {
    project.delete(layout.buildDirectory.dir("package"))
}

setupBuilder {
    vendor = project.group.toString()
    application = "Cards of legends"
    appIdentifier = "COL"
    version = "1.0.0.0"
    bundleJre = 1.8
}

tasks.msi {
    dependsOn("createPackage")
    from (layout.buildDirectory.dir("package").get().asFile.absolutePath) {
        include("**/*")
    }
    setupBuilder.desktopStarter {
        displayName = "Cards of legends"
        executable = "launcher/CardsOfLegendsLauncher.exe"
        location = com.inet.gradle.setup.abstracts.DesktopStarter.Location.DesktopDir
        icons = "resources/icon.ico"
    }
}

tasks.register<DefaultTask>("cleanMsi") {
    project.delete(layout.buildDirectory.dir("distributions"))
    project.delete(layout.buildDirectory.dir("tmp/msi"))
}