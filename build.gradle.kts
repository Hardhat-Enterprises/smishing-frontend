// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        mavenCentral()
        google()
        // Add other repositories here
    }
    dependencies {
        classpath("com.chaquo.python:gradle:15.0.1")

        // Add other classpaths here
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}