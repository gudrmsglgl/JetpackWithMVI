package dependencies.dependencies

import dependencies.Versions

object KTXDependencies {

    val ktx = "androidx.core:core-ktx:${Versions.ktx}"
    val room_ktx = "androidx.room:room-ktx:${Versions.room}" // suspend fun
    val livedata_ktx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle_version}" // coroutine livedata
    val fragment_ktx = "androidx.fragment:fragment-ktx:${Versions.fragment_ktx_version}" // by viewmodels
    val viewmodel_ktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle_version}"
    val databinding_ktx = "com.github.wada811:DataBinding-ktx:${Versions.databinding_ktx}"
}