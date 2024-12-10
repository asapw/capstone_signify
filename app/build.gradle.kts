import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.mycapstone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mycapstone"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a"))
        }
        val properties = Properties().apply {
            load(project.rootProject.file("local.properties").inputStream())
        }

        buildConfigField("String", "GEMINI_API_KEY", "\"${properties.getProperty("geminiKey")}\"")
        buildConfigField("String", "NEWS_API_KEY", "\"${properties.getProperty("newsKey")}\"")
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
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    kotlinOptions {
        jvmTarget = "18"
    }

    buildFeatures {
        viewBinding = true
        mlModelBinding = true
        buildConfig = true
    }


}

dependencies {
    implementation (libs.androidx.media3.exoplayer)
    implementation (libs.androidx.media3.ui)
    implementation (libs.androidx.media3.common)

    // Gemini
    implementation("com.google.ai.client.generativeai:generativeai:0.4.0")


    implementation (libs.gson)
    // Retrofit for networking
    implementation(libs.retrofit)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Gson Converter for JSON serialization/deserialization
    implementation(libs.converter.gson)

    // CardView
    implementation ("androidx.cardview:cardview:1.0.0")

    // Lottie Animation
    implementation("com.airbnb.android:lottie:6.6.0")

    // Blur View
    implementation ("com.github.Dimezis:BlurView:version-2.0.5")

    // Firebase Storage for image upload
    implementation (libs.firebase.storage)

    // Glide for image loading
    implementation (libs.glide)
    implementation(libs.androidx.media3.exoplayer)
    annotationProcessor (libs.compiler)

    // Activity Result API for handling image selection
    implementation (libs.androidx.activity.ktx)

    implementation (libs.androidx.lifecycle.viewmodel.ktx)


    // MediaPipe
    implementation("com.google.mediapipe:tasks-vision:0.20230731")

    //color
    implementation (libs.dotsindicator)

    implementation(libs.guava)


    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.firestore)
    implementation(libs.firebase.auth)

    // CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // TensorFlow Lite
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // ViewPager2 and Indicator
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("com.tbuonomo:dotsindicator:4.3")

    // AndroidX and Material
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
