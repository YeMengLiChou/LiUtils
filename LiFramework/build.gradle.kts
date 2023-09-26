@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    `maven-publish`
}

afterEvaluate {
    publishing {
        publications {
            register("release", MavenPublication::class) {
                // 从当前 module 的 release 包中发布
                from(components["release"])
                groupId = "com.github.grimrise"
                artifactId = "release"
                version = "0.0.0"
            }
        }
    }
}

android {
    namespace = "com.li.framework"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    api(libs.activity.ktx)
    api(libs.fragment.ktx)
    api(libs.bundles.viewModel)
    api(libs.bundles.coroutines)
    api(libs.glide)
    api(libs.bundles.retrofit)
    api(libs.fastKv)

    implementation(libs.lifecycle.runtime)
    implementation(libs.moshi)
    implementation(libs.xlog)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}