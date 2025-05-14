buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.chaquo.python:gradle:15.0.1")
        classpath("com.google.gms:google-services:4.4.1")
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}
