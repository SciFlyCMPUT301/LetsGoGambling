plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.eventbooking"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.eventbooking"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildToolsVersion = "35.0.0"
}



dependencies {
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.google.zxing:core:3.3.3")
    implementation("com.google.firebase:firebase-bom:33.4.0")
    implementation("com.google.firebase:firebase-storage:21.0.1")
    implementation("com.squareup.picasso:picasso:2.8")
    //implementation(files("C:\\Users\\pilck\\AppData\\Local\\Android\\Sdk\\platforms\\android-35\\android.jar"))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.qr.scanner)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.runtime)
    implementation(libs.navigation.fragment)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.qr.scanner)
    implementation("com.google.firebase:firebase-inappmessaging:21.0.1")
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:2.28.2")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("org.mockito:mockito-core:2.28.2")
}