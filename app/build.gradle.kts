import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
//    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "chromahub.rhythm.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "chromahub.rhythm.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 46360967
        versionName = "4.6.360.967"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Product flavors for different distribution channels
    flavorDimensions += "distribution"
    
    productFlavors {
        create("fdroid") {
            dimension = "distribution"
            applicationId = "chromahub.rhythm.app"
            
            // F-Droid build: Enable all features (FOSS ethos)
            buildConfigField("boolean", "ENABLE_YOUTUBE_MUSIC", "true")
            buildConfigField("boolean", "ENABLE_APPLE_MUSIC", "true")
            buildConfigField("boolean", "ENABLE_DEEZER", "true")
            buildConfigField("boolean", "ENABLE_LRCLIB", "true")
            buildConfigField("boolean", "ENABLE_SPOTIFY_SEARCH", "true")
            buildConfigField("String", "FLAVOR", "\"fdroid\"")
            
            versionNameSuffix = "-fdroid"
        }
        
        create("github") {
            dimension = "distribution"
            applicationId = "chromahub.rhythm.app"
            
            // GitHub releases: Enable all features (same as F-Droid)
            buildConfigField("boolean", "ENABLE_YOUTUBE_MUSIC", "true")
            buildConfigField("boolean", "ENABLE_APPLE_MUSIC", "true")
            buildConfigField("boolean", "ENABLE_DEEZER", "true")
            buildConfigField("boolean", "ENABLE_LRCLIB", "true")
            buildConfigField("boolean", "ENABLE_SPOTIFY_SEARCH", "true")
            buildConfigField("String", "FLAVOR", "\"github\"")
            
            versionNameSuffix = "-gh"
        }
    }

    val signingProperties = getProperties(".config/keystore.properties")
    val releaseSigning = if (signingProperties != null) {
        signingConfigs.create("release") {
            keyAlias = signingProperties.property("key_alias")
            keyPassword = signingProperties.property("key_password")
            storePassword = signingProperties.property("store_password")
            storeFile = rootProject.file(signingProperties.property("store_file"))
        }
    } else {
        signingConfigs.getByName("debug")
    }

    defaultConfig {
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = releaseSigning
//            ndk {
//                debugSymbolLevel = "SYMBOL_TABLE"
//            }
            // Reproducible builds: disable build timestamp
            if (System.getenv("CI") == "true" || System.getenv("BUILD_REPRODUCIBLE") == "true") {
                // Use a fixed timestamp for reproducible builds  
                tasks.configureEach {
                    // Disable timestamps in bundle reports for reproducible builds
                    if (name.contains("BundleReport", ignoreCase = true)) {
                        enabled = false
                    }
                }
            }
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            //isMinifyEnabled = false
            //isDebuggable = true
            signingConfig = releaseSigning
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            freeCompilerArgs.addAll(
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
            )
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    dependenciesInfo {
        // Disables dependency metadata when building APKs (for IzzyOnDroid/F-Droid)
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles
        includeInBundle = false
    }

    packaging {
        resources {
            merges += "/META-INF/INDEX.LIST"
            merges += "**/io.netty.versions.properties"
        }
    }

    // ABI splits: create smaller per-architecture APKs (reduces size by ~5–10 MB each)
    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
            isUniversalApk = true // also keep a universal APK for IzzyOnDroid/F-Droid
        }
    }

    applicationVariants.all {
        outputs.all {
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                "Rhythm-${defaultConfig.versionName}-${name}.apk"
        }
    }
}

dependencies {
    // Desugaring library
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation("androidx.core:core:1.18.0") // Downgrade core dependency for compatibility
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.appcompat:appcompat:1.7.1") // For AppCompatDelegate locale support
    
    // Compose dependencies
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    
    // Material 3 dependencies
    implementation(platform(libs.androidx.compose.bom))
    implementation("androidx.compose.material3:material3-android:1.5.0-alpha16")
    implementation("androidx.compose.material3:material3-window-size-class-android:1.5.0-alpha16")
    implementation("com.google.android.material:material:1.13.0-alpha05")

    // Media3 dependencies
    implementation("androidx.media3:media3-exoplayer:1.10.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.10.0")
    implementation("androidx.media3:media3-ui:1.10.0")
    implementation("androidx.media3:media3-session:1.10.0")
    implementation("org.jellyfin.media3:media3-ffmpeg-decoder:1.9.0+1")
    
    // Icons
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("androidx.palette:palette-ktx:1.0.0")
    
    // Glance for modern widgets
    implementation("androidx.glance:glance-appwidget:1.1.1")
    implementation("androidx.glance:glance-material3:1.1.1")
    
    // Physics-based animations
    implementation("androidx.compose.animation:animation:1.10.6")
    //noinspection GradleDependency
    implementation("androidx.compose.animation:animation-graphics:1.8.3")
    implementation(libs.androidx.compose.animation.core)
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.9.7")
    
    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")
    
    // Fragment
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    
    // MediaRouter
    implementation("androidx.mediarouter:mediarouter:1.8.1")
    
    // Ktor for HTTP server (Cast media serving)
    implementation("io.ktor:ktor-server-core:3.4.2")
    implementation("io.ktor:ktor-server-netty:3.4.2")
    
    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.7.0")
    
    // Audio metadata editing
    implementation("net.jthink:jaudiotagger:3.0.1")
    
    // Network
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    implementation("com.squareup.okhttp3:logging-interceptor:5.3.2")
    implementation("com.google.code.gson:gson:2.13.2")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
//    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // Coroutines for async operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation(libs.androidx.foundation.layout)
    
    // WorkManager for background tasks
    implementation("androidx.work:work-runtime-ktx:2.11.2")
    
    // Room database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    
    // LeakCanary for memory leak detection (debug builds only)
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")
}

fun getProperties(fileName: String): Properties? {
    val file = rootProject.file(fileName)
    return if (file.exists()) {
        Properties().also { properties ->
            file.inputStream().use { properties.load(it) }
        }
    } else null
}

fun Properties.property(key: String) =
    this.getProperty(key) ?: "$key missing"