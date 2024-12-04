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

    packaging {
        resources {
            excludes += "mockito-extensions/org.mockito.plugins.StackTraceCleanerProvider"
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildToolsVersion = "35.0.0"
}

tasks.register<Javadoc>("generateJavadoc") {
    dependsOn("assembleDebug")

    source = files(android.sourceSets["main"].java.srcDirs).asFileTree
    classpath = files(android.bootClasspath.joinToString(File.separator))

    // Exclude R class
    exclude("**/R.class")
}

//configurations.all {
//    resolutionStrategy {
//        force("com.google.android:annotations:4.1.1.4")  // Or update to a version compatible with your project
//    }
//}

dependencies {
//    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.google.zxing:core:3.3.3")
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
//    implementation("com.google.firebase:firebase-storage:21.0.1")
//    implementation("com.google.firebase:firebase-storage") {
//        exclude(group = "com.google.android", module = "annotations")
//    }
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.android:annotations:4.1.1.4")
//    implementation("com.squareup.picasso:picasso:2.8") {
//        exclude(group = "com.google.android", module = "annotations")
//    }
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.google.firebase:firebase-messaging")
    implementation("org.osmdroid:osmdroid-android:6.1.20")
//    implementation("org.osmdroid:osmdroid-android")
//    implementation("com.google.protobuf:protobuf-javalite:4.29.0")

//    implementation(files("C:\\Users\\pilck\\AppData\\Local\\Android\\Sdk\\platforms\\android-35\\android.jar"))
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
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("org.mockito:mockito-android:5.14.2")
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
//    androidTestImplementation("org.mockito:mockito-core:2.28.2")
    testImplementation("org.mockito:mockito-core:2.28.2")
//    testImplementation("org.robolectric:robolectric:4.10")
    testImplementation("org.mockito:mockito-inline:5.0.0")
    testImplementation("org.mockito:mockito-android:2.28.2")
//    testImplementation("com.linkedin.dexmaker:dexmaker-mockito:2.28.1")
    testImplementation("org.powermock:powermock-module-junit4:2.0.9")
    testImplementation("org.powermock:powermock-api-mockito2:2.0.9")




    //Trial section
//    androidTestImplementation("org.mockito:mockito-core:5.4.0")
//    androidTestImplementation("com.linkedin.dexmaker:dexmaker-mockito-inline:2.28.1")
//    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
//    androidTestImplementation("org.mockito:mockito-core:2.28.2")
//    androidTestImplementation("org.mockito:mockito-core:2.28.2")
//    androidTestImplementation("org.mockito:mockito-android:2.28.2")
//    androidTestImplementation("com.linkedin.dexmaker:dexmaker-mockito:2.28.1")
//    androidTestImplementation("org.mockito:mockito-core:4.8.1")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
////    androidTestImplementation("org.mockito:mockito-android:2.28.2")
//    androidTestImplementation("org.mockito:mockito-android:5.0.0")
//    androidTestImplementation("com.linkedin.dexmaker:dexmaker-mockito-inline:2.28.1")
//    androidTestImplementation("com.linkedin.dexmaker:dexmaker-mockito:2.28.1")
    androidTestImplementation("org.mockito:mockito-core:2.28.2")
//    testImplementation("org.robolectric:robolectric:4.10")
    androidTestImplementation("org.mockito:mockito-inline:5.0.0")
    androidTestImplementation("org.mockito:mockito-android:2.28.2")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestImplementation("androidx.test:rules:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.4.0")
}