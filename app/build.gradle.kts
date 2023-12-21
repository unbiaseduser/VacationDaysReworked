plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.devtools.ksp") version "1.9.21-1.0.16"
    id("de.mannodermaus.android-junit5") version "1.9.3.0"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

android {
    namespace = "com.sixtyninefourtwenty.vacationdaysreworked"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sixtyninefourtwenty.vacationdaysreworked"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnit5Builder"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles += getDefaultProguardFile("proguard-android-optimize.txt")
            proguardFiles += file("proguard-rules.pro")
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
    packaging {
        resources {
            excludes += ("META-INF/atomicfu.kotlin_module")
        }
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    val navigation = "2.7.6"
    implementation("androidx.navigation:navigation-fragment-ktx:$navigation")
    implementation("androidx.navigation:navigation-ui-ktx:$navigation")
    val room = "2.6.1"
    implementation("androidx.room:room-runtime:$room")
    implementation("androidx.room:room-ktx:$room")
    ksp("androidx.room:room-compiler:$room")
    val lifecycle = "2.6.2"
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("com.kizitonwose.calendar:view:2.4.1")
    implementation("com.louiscad.splitties:splitties-fun-pack-android-material-components:3.0.0")
    implementation ("com.github.cachapa:ExpandableLayout:2.9.2")
    implementation("com.github.Kennyc1012:MultiStateView:2.2.0")
    implementation("com.github.unbiaseduser:bottom-sheet-alert-dialog:1.1")
    implementation("com.github.unbiaseduser:base-fragments:1.0")
    implementation("com.github.unbiaseduser:stuff:1.0")
    val libIntegration = "1.0.2"
    implementation("com.github.unbiaseduser.library-integrations:custom-preferences-theming-integration:$libIntegration")
    implementation("com.github.unbiaseduser.library-integrations:theming-preference-integration:$libIntegration")
    testImplementation("junit:junit:4.13.2")
    val junitJupiter = "5.10.1"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiter")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiter")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiter")
    val junit5Android = "1.4.0"
    androidTestImplementation("de.mannodermaus.junit5:android-test-core:$junit5Android")
    androidTestRuntimeOnly("de.mannodermaus.junit5:android-test-runner:$junit5Android")
    androidTestRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiter")
    debugImplementation("androidx.fragment:fragment-testing:1.6.2")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

}