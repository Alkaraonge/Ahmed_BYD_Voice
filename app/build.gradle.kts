plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ahmed.bydvoice"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ahmed.bydvoice"
        minSdk = 24
        targetSdk = 34
        versionCode = 170
        versionName = "1.7.0"
    }

    signingConfigs {
        // الأولوية: مفتاح دائم من متغيرات البيئة (GitHub Secrets).
        // إذا غير مهيأ نستخدم مفتاح debug التلقائي — الـAPK يبقى قابل للتثبيت.
        // لتوليد مفتاح: keytool -genkeypair -keystore release.jks -alias byd ...
        create("release") {
            val ksPath = System.getenv("KEYSTORE_PATH")
            val ksPass = System.getenv("KEYSTORE_PASSWORD")
            val kAlias = System.getenv("KEY_ALIAS")
            val kPass = System.getenv("KEY_PASSWORD")
            if (!ksPath.isNullOrBlank() && !ksPass.isNullOrBlank() &&
                !kAlias.isNullOrBlank() && !kPass.isNullOrBlank()
            ) {
                storeFile = file(ksPath)
                storePassword = ksPass
                keyAlias = kAlias
                keyPassword = kPass
            } else {
                val fallbackKs = layout.buildDirectory.file("tmp/fallback-debug.jks").get().asFile
                if (!fallbackKs.exists()) {
                    fallbackKs.parentFile.mkdirs()
                    ProcessBuilder(
                        "keytool", "-genkeypair", "-v",
                        "-keystore", fallbackKs.absolutePath,
                        "-alias", "androiddebugkey",
                        "-keyalg", "RSA", "-keysize", "2048", "-validity", "10000",
                        "-storepass", "android", "-keypass", "android",
                        "-dname", "CN=Android Debug,O=Android,C=US"
                    ).redirectErrorStream(true).start().waitFor()
                }
                storeFile = fallbackKs
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:theme"))
    implementation(project(":core:update"))
    implementation(project(":core:voice"))
    implementation(libs.androidx.core.ktx)

    // التعرف الصوتي الأوفلاين (Vosk) — مكتبة + موديل ينزّل داخل التطبيق مرة وحدة
    implementation("net.java.dev.jna:jna:5.13.0@aar")
    implementation("com.alphacephei:vosk-android:0.3.47") {
        exclude(group = "net.java.dev.jna", module = "jna")
    }

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
