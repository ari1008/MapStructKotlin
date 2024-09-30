plugins {
    kotlin("jvm") version "2.0.10"
    id("com.google.devtools.ksp") version "2.0.10-1.0.24"
    application
}

group = "com.test"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Configuration pour voir les logs de Gradle
gradle.startParameter.showStacktrace = ShowStacktrace.ALWAYS
gradle.startParameter.logLevel = LogLevel.INFO

kotlin.sourceSets.main {
    kotlin.srcDirs(
        file("${layout.buildDirectory}/generated/ksp/main/kotlin"),
    )
}

ksp {
    arg("ignoreGenericArgs", "false")
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
    implementation(project(":annotation"))
    ksp(project(":processor"))
    kspTest(project(":processor"))
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:2.0.20")

}
ksp {
    arg("ignoreGenericArgs", "false")
}
tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":annotation"))
    ksp(project(":processor"))
}
application {
    mainClass.set("com.test.MainKt")
}

