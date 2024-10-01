plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.brucelet.spacetrader"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.brucelet.spacetrader"
        minSdk = 14
        targetSdk = 28
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-project.txt"
            )
        }
    }
    buildFeatures {
        buildConfig = true
    }

    lint {
        baseline = file("lint-baseline.xml")
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.0.0")

//    debugCompile("com.squareup.leakcanary:leakcanary-android:1.3.1")
//    releaseCompile("com.squareup.leakcanary:leakcanary-android-no-op:1.3.1")
}
