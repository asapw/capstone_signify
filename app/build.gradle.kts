plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.mycapstone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mycapstone"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        viewBinding = true
        mlModelBinding = true
    }
}

dependencies {


// CameraX dependencies
    implementation (libs.androidx.camera.core)
    implementation (libs.camera.camera2) // Required for Camera2 implementation
    implementation (libs.androidx.camera.lifecycle)  // For lifecycle support
    implementation (libs.androidx.camera.view) // For CameraX view (if using)
    
    implementation (libs.tensorflow.lite)

    // Splash Screen
    implementation ("androidx.core:core-splashscreen:1.0.1")


    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // Indicator
    implementation("com.tbuonomo:dotsindicator:4.3")

    implementation(platform(libs.firebase.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}