plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.brucelet.spacetrader"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.brucelet.spacetrader"
        minSdk = 9
        targetSdk = 23
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
    implementation("com.android.support:appcompat-v7:24.2.1")

//    debugCompile("com.squareup.leakcanary:leakcanary-android:1.3.1")
//    releaseCompile("com.squareup.leakcanary:leakcanary-android-no-op:1.3.1")
}
