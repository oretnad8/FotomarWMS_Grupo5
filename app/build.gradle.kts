plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.pneuma.fotomarwms_grupo5"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.pneuma.fotomarwms_grupo5"
        minSdk = 33
        targetSdk = 36
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // ========== NAVEGACIÓN Y ARQUITECTURA ==========
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // ========== ANDROID CORE ==========
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ========== COMPOSE ==========
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended")

    // ========== ROOM ==========
    val room_version = "2.6.1" // Usa la última versión estable
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    // Opcional - Soporte para Kotlin Symbol Processing (KSP) -> Recomendado
    ksp("androidx.room:room-compiler:$room_version")
    // Opcional - Soporte para Coroutines con Room
    implementation("androidx.room:room-ktx:$room_version")
    // Opcional - Paging 3
    implementation("androidx.room:room-paging:$room_version")
    // Opcional - Testing
    testImplementation("androidx.room:room-testing:$room_version")

    // ========== CAMERAX (Para escaneo de códigos) ==========
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.5.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // ========== ML KIT (Escaneo de códigos de barras) ==========
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    // ========== ACCOMPANIST (Para permisos en Compose) ==========
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    // ========== TESTING ==========
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // ========== NETWORKING (Retrofit, OkHttp, Gson) ==========
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // Para ver logs de red

    // ========== DEBUG ==========
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}