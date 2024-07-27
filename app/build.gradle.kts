plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.brucelet.spacetrader"
    compileSdk = 23

    defaultConfig {
        applicationId = "com.brucelet.spacetrader"
        minSdk = 8
        targetSdk = 23
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-project.txt")
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation("com.android.support:appcompat-v7:23.2.1")

//    debugCompile("com.squareup.leakcanary:leakcanary-android:1.3.1")
//    releaseCompile("com.squareup.leakcanary:leakcanary-android-no-op:1.3.1")
}
