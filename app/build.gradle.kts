plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
    id("com.google.devtools.ksp")
}

android {

    namespace = "com.wissi"
    compileSdk = 35

    defaultConfig {

        applicationId = "com.wissi"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        release {

            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {

        compose = true
    }

    kotlinOptions {

        jvmTarget = "17"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")

    implementation("com.google.code.gson:gson:2.10.1")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.3")

    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3"
    )

    implementation(
        "androidx.activity:activity-compose:1.9.0"
    )

    implementation(
        platform("androidx.compose:compose-bom:2024.06.00")
    )

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")

    implementation(
        "androidx.compose.ui:ui-tooling-preview"
    )

    implementation(
        "androidx.compose.material3:material3"
    )

    implementation(
        "androidx.navigation:navigation-compose:2.7.7"
    )

    implementation(
        "androidx.navigation:navigation-runtime-ktx:2.7.7"
    )

    // ===== ROOM =====

    implementation(
        "androidx.room:room-runtime:2.6.1"
    )

    implementation(
        "androidx.room:room-ktx:2.6.1"
    )

    ksp(
        "androidx.room:room-compiler:2.6.1"
    )

    // ===== SERIALIZATION =====

    implementation(
        "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3"
    )

    // ===== SUPABASE =====

    implementation(
        platform(
            "io.github.jan-tennert.supabase:bom:2.6.1"
        )
    )

    implementation(
        "io.github.jan-tennert.supabase:postgrest-kt"
    )

    implementation(
        "io.github.jan-tennert.supabase:storage-kt"
    )



    // ===== KTOR =====

    implementation(
        "io.ktor:ktor-client-core:2.3.12"
    )

    implementation(
        "io.ktor:ktor-client-okhttp:2.3.12"
    )

    implementation(
        "io.ktor:ktor-client-content-negotiation:2.3.12"
    )

    // ===== COIL =====

    implementation(
        "io.coil-kt:coil-compose:2.5.0"
    )

    // ===== TESTS =====

    testImplementation(
        "junit:junit:4.13.2"
    )

    androidTestImplementation(
        "androidx.test.ext:junit:1.1.5"
    )

    androidTestImplementation(
        "androidx.test.espresso:espresso-core:3.5.1"
    )

    androidTestImplementation(
        platform("androidx.compose:compose-bom:2024.06.00")
    )

    androidTestImplementation(
        "androidx.compose.ui:ui-test-junit4"
    )

    debugImplementation(
        "androidx.compose.ui:ui-tooling"
    )

    debugImplementation(
        "androidx.compose.ui:ui-test-manifest"
    )
}