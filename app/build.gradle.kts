plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.kayodedaniel.nestnews"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kayodedaniel.nestnews"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true
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
    buildFeatures{
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.kotlin.stdlib)
    implementation(libs.squareup.retrofit2)
    implementation(libs.squareup.retrofit2.converter.gson)
    implementation(libs.squareup.picasso)
    implementation(libs.androidx.recyclerview)
    implementation(libs.ssp.android)
    implementation(libs.multidex)
    implementation(libs.retrofit)
    implementation(libs.converterscalars)
    testImplementation ("junit:junit:4.13.2")


    testImplementation("org.mockito:mockito-core:4.10.0")
    testImplementation("org.mockito:mockito-inline:4.10.0")
    androidTestImplementation("org.mockito:mockito-android:4.10.0")

    // Espresso UI testing
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.0")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")


    // Firebase dependencies
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.ssp.android)
    implementation(libs.makeramen.roundedimageview)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore)
    implementation("junit:junit:4.12")
    implementation(libs.androidx.junit.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}