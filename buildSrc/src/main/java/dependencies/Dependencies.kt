package dependencies.dependencies
import dependencies.Versions

object Dependencies {
    val kotlin_standard_library = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    val kotlin_reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    val kotlin_coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines_version}"
    val kotlin_coroutines_android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines_version}"
    val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    val navigation_fragment = "androidx.navigation:navigation-fragment-ktx:${Versions.nav_components}"
    val navigation_runtime = "androidx.navigation:navigation-runtime:${Versions.nav_components}"
    val navigation_ui = "androidx.navigation:navigation-ui-ktx:${Versions.nav_components}"
    val material_dialogs = "com.afollestad.material-dialogs:core:${Versions.material_dialogs}"
    val material_dialogs_input = "com.afollestad.material-dialogs:input:${Versions.material_dialogs}"
    val room_runtime = "androidx.room:room-runtime:${Versions.room}"
    val play_core = "com.google.android.play:core:${Versions.play_core}"
    val leak_canary = "com.squareup.leakcanary:leakcanary-android:${Versions.leak_canary}"
    val lifecycle_runtime = "androidx.lifecycle:lifecycle-runtime:${Versions.lifecycle_version}"
    val lifecycle_coroutines = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle_version}"
    val lifecycle_viewmodel_savestate = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.savestate_verson}"
    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit2_version}"
    val retrofit_gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit2_version}"
    val rx_kotlin = "io.reactivex.rxjava3:rxkotlin:${Versions.rx_versin}"
    val rx_android = "io.reactivex.rxjava3:rxandroid:${Versions.rx_versin}"
    val rxbinding = "com.jakewharton.rxbinding3:rxbinding:${Versions.rxbinding_version}"
    val rxbinding_recycelerview = "com.jakewharton.rxbinding3:rxbinding-recyclerview:${Versions.rxbinding_version}"
    val okio = "com.squareup.okio:okio:${Versions.okio_version}"
    val glide = "com.github.bumptech.glide:glide:${Versions.glide_version}"
    val compressor = "id.zelory:compressor:2.1.0"
    val image_cropper = "com.theartofdev.edmodo:android-image-cropper:2.8.+"
}