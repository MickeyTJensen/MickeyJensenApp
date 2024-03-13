// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}
buildscript {
    repositories {
        google()
        mavenCentral()
        // Add any custom repositories for buildscript dependencies
    }
    // Add buildscript dependencies if needed
    dependencies {
        // Example: classpath "com.example:custom-plugin:1.0"
    }
}
allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}