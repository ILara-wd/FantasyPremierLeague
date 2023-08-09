plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("io.realm.kotlin")
    id("com.google.devtools.ksp")
    id("com.rickclephas.kmp.nativecoroutines")
}


android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "dev.johnoreilly.common"
}

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "FantasyPremierLeagueKit"

            // re. https://youtrack.jetbrains.com/issue/KT-60230/Native-unknown-options-iossimulatorversionmin-sdkversion-with-Xcode-15-beta-3
            // due to be fixed in Kotlin 1.9.10
            if (System.getenv("XCODE_VERSION_MAJOR") == "1500") {
                linkerOpts += "-ld64"
            }
        }
    }

    androidTarget()
    jvm()


    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.serialization)
                api(libs.kotlinx.datetime)

                api(libs.koin.core)
                implementation(libs.koin.test)

                implementation(libs.bundles.ktor.common)
                implementation(libs.realm)
                api(libs.bundles.multiplatformSettings)
                api(libs.kermit)
            }
        }

        val commonTest by getting {
            dependencies {
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.android)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.client.java)
                implementation(libs.slf4j)

                implementation("org.nield:kotlin-statistics:1.2.1")
                implementation("org.ojalgo:okalgo:0.0.2")
                implementation("org.jetbrains.kotlinx:multik-api:0.1.1")
                implementation("org.jetbrains.kotlinx:multik-jvm:0.1.1")
            }
        }


        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.ktor.client.ios)
            }
        }

    }
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
}
