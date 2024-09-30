plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "StateOfArt"
include("annotation")
include("project")
include("processor")
