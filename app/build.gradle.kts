plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdkVersion(23)

    defaultConfig {
        applicationId = "com.brucelet.spacetrader"
        minSdkVersion(8)
        targetSdkVersion(23)
    }

    buildTypes {
        release {
            minifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-project.txt")
        }
    }
    namespace = "com.brucelet.spacetrader"

}

dependencies {
    implementation("com.android.support:appcompat-v7:23.2.1")

//    debugCompile("com.squareup.leakcanary:leakcanary-android:1.3.1")
//    releaseCompile("com.squareup.leakcanary:leakcanary-android-no-op:1.3.1")
}
