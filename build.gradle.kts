// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        mavenCentral()
        google()
        // Add other repositories here
        maven {
            setUrl("https://maven.localazy.com/repository/release/")
        }
    }
    dependencies {
        classpath("com.chaquo.python:gradle:15.0.1")

        // Add other classpaths here
        classpath("com.localazy:gradle:2.0.3")
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}