plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.app.core.database"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {
    implementation(libs.bundles.database)
    ksp(libs.androidx.room.compiler)
    implementation(libs.kotlinx.coroutines.core)


    testImplementation(libs.bundles.unit.test)
    testImplementation(libs.bundles.unit.test.robolectric)
    androidTestImplementation(libs.bundles.android.test)
}