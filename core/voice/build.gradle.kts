plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.ahmed.bydvoice.core.voice"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    api(project(":core:theme"))
    testImplementation("junit:junit:4.13.2")
    // المنطق نقي (JVM) — التعرف الصوتي نفسه بالواجهة (app)، هنا فقط تفسير النص
}
