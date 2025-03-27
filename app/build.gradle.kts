plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.chaquo.python")
}

android {
    ndkVersion = "27.0.11718014"
    namespace = "com.example.smishingdetectionapp"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        ndk {
            // On Apple silicon, you can omit x86_64.
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
        applicationId = "com.example.smishingdetectionapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Merge the vectorDrawables from both branches
        vectorDrawables {
            useSupportLibrary = true
        }

        // Include the buildConfigFields from master
        buildConfigField("String", "EMAIL", "\"smsphishing8@gmail.com\"")
        buildConfigField("String", "EMAILPASSWORD", "\"xedr gaek jdsv ujxw\"")
        buildConfigField("String", "SERVERIP", "\"http:192.168.?.?:3000\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }

    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/DEPENDENCIES",
                "/META-INF/LICENSE",
                "/META-INF/LICENSE.txt",
                "/META-INF/NOTICE",
                "/META-INF/NOTICE.txt",
                "META-INF/INDEX.LIST"
            )
        }
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/python")
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.annotation)
    implementation(libs.preference)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation ("androidx.core:core-ktx:1.6.0")
    implementation(libs.activity)
    implementation(files("libs/sqliteassethelper-2.0.1.jar"))
    implementation(libs.biometric)
    implementation(libs.play.services.tasks)

    // Merge testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    // External libraries from feature/ocr-functionality
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.simplexml)

    // Google Cloud Vision API
    implementation(libs.google.cloud.vision.v11000)

    // Google Cloud Translate API
    implementation(libs.google.cloud.translate.v1950)

    // gRPC and Protobuf dependencies
    implementation(libs.grpc.okhttp)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.auth)
    implementation(libs.grpc.core)

    // Additional dependencies from master
    implementation(files("libs/sqliteassethelper-2.0.1.jar"))
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.retrofit2:converter-simplexml:2.11.0")
    implementation("com.google.android.material:material:1.2.0-alpha02")
    implementation(files("libs/activation.jar"))
    implementation(files("libs/additionnal.jar"))
    implementation(files("libs/mail.jar"))
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:html:4.6.2")
    implementation("io.noties.markwon:image:4.6.2")
    implementation("com.google.android.gms:play-services-auth:20.0.0")

}
