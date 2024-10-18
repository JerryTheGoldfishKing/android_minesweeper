plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "org.goldfish.minesweeper_android_01"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.goldfish.minesweeper_android_01"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation ("androidx.lifecycle:lifecycle-process:2.3.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
dependencies{
    implementation(files("src/main/libs/AMap3DMap_10.0.900_AMapSearch_9.7.3_AMapLocation_6.4.7_20240816.jar"))
}