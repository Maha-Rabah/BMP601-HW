plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.ite_bmp601_hw_s24"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ite_bmp601_hw_s24"
        minSdk = 27
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.filament.android)
    implementation(libs.impress)
    implementation(libs.navigation.runtime)
    implementation(libs.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.navigation.fragment)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}