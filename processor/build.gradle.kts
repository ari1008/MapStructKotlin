plugins {
    kotlin("jvm") version "2.0.10"
}

group = "com.test"
version = "1.0-SNAPSHOT"

// Configuration pour voir les logs de Gradle
gradle.startParameter.showStacktrace = ShowStacktrace.ALWAYS
gradle.startParameter.logLevel = LogLevel.INFO

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":annotation"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.0.10-1.0.24")
    implementation("com.squareup:kotlinpoet-ksp:1.18.1")
    implementation("org.jetbrains.kotlin:kotlin-metadata-jvm:2.0.10")


}