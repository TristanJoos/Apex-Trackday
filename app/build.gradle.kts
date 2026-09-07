plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.st_client_mobile_tristanjooshowest"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.st_client_mobile_tristanjooshowest"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "API_BASE_URL", "\"${project.findProperty("API_BASE_URL") ?: ""}\"")
        buildConfigField("String", "AMQPusername", "\"${project.findProperty("AMQPusername") ?: ""}\"")
        buildConfigField("String", "AMQPpassword", "\"${project.findProperty("AMQPpassword") ?: ""}\"")
        buildConfigField("String", "AMQPurl", "\"${project.findProperty("AMQPurl") ?: ""}\"")
        buildConfigField("String", "AMQPpublishtopic", "\"${project.findProperty("AMQPpublishtopic") ?: ""}\"")
        buildConfigField("String", "AMQPsubscribetopic", "\"${project.findProperty("AMQPsubscribetopic") ?: ""}\"")
        buildConfigField("String", "AMQPconsumertag", "\"${project.findProperty("AMQPconsumertag") ?: ""}\"")
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.preferences.datastore)
    implementation(libs.play.services.maps)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.work.runtime.ktx)
    ksp(libs.androidx.room.compiler)

    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Location & Maps
    implementation(libs.play.services.location)
    implementation(libs.mapbox.android)
    implementation(libs.mapbox.extension.compose)

    // Networking & Moshi
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.okhttp)

    // Messaging
    implementation(libs.rabbitmq.amqp.client)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.junit.ktx)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //lucide icons
    implementation("com.composables:icons-lucide:1.1.0")

    implementation("com.google.zxing:core:3.5.3")

    //weather api
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("androidx.core:core-splashscreen:1.0.1")
}
