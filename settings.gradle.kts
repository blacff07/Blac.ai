pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Correct JitPack configuration for Prism4j
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "Blac.ai"
include(":app")