plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id ("kotlinx-serialization")
}

android {
    namespace = "com.example.socialmedia"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.social.socialmedia"
        minSdk = 24
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    packagingOptions {
        exclude("META-INF/LICENSE.md")
        exclude("META-INF/NOTICE.md")
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.databinding:databinding-compiler-common:8.2.2")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.compose.foundation:foundation-android:1.6.4")

    implementation("androidx.media3:media3-exoplayer:1.3.0")
    implementation ("androidx.media3:media3-ui:1.3.0")
    implementation("androidx.datastore:datastore-core-android:1.1.0-beta02")
    implementation("com.google.firebase:firebase-database-ktx:20.3.1")
    implementation("androidx.activity:activity:1.8.0")
    implementation("com.android.billingclient:billing-ktx:6.2.0")
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    implementation("com.google.firebase:firebase-database-ktx:20.3.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    //firebase
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation ("com.google.android.gms:play-services-auth:21.0.0")
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2")
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation ("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.firebase:firebase-dynamic-links-ktx:21.2.0")
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2")
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.9")
    implementation ("com.google.firebase:firebase-analytics-ktx:21.5.1")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")

    //Koin
    implementation("io.insert-koin:koin-android:3.5.0")

    // Zegoda
   /* implementation("com.github.ZEGOCLOUD:zego_uikit_prebuilt_call_android:2.10.4")
    implementation("com.github.ZEGOCLOUD:zego_uikit_signaling_plugin_android:")*/

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.10.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:converter-gson:2.10.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //facebook
    implementation ("com.facebook.android:facebook-login:16.3.0")
    implementation ("com.facebook.android:facebook-android-sdk:16.3.0")
    implementation("com.facebook.android:facebook-login:16.0.0")
    implementation("com.facebook.android:facebook-android-sdk:16.0.0")
    // circle image view
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //Lottie
    implementation("com.airbnb.android:lottie:3.7.0")
    implementation("com.github.bumptech.glide:glide:4.8.0")

    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation("com.karumi:dexter:6.2.3")
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation ("com.karumi:dexter:6.2.3")
    implementation ("com.intuit.sdp:sdp-android:1.1.1")


    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    //lottie
    implementation ("com.airbnb.android:lottie:6.4.0")

    //Picasso
    implementation("com.squareup.picasso:picasso:2.71828")

    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
    // check email
    implementation("com.google.guava:guava:31.1-jre")
    // load ảnh từ bộ nhớ máy
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation ("com.google.guava:guava:31.1-jre")

    implementation ("androidx.activity:activity-ktx:1.8.2")
    implementation ("org.greenrobot:eventbus:3.3.1")

    //Android Room
    implementation ("androidx.room:room-runtime:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-common:2.6.1")

    //dagger
    implementation ("com.google.dagger:dagger:2.51")
    implementation ("com.google.dagger:dagger-android-support:2.51")
    kapt ("com.google.dagger:dagger-android-processor:2.51")
    kapt ("com.google.dagger:dagger-compiler:2.51")

    implementation("com.vanniktech:emoji-google:0.18.0")
    implementation("com.github.MikeOrtiz:TouchImageView:1.4.1")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
}