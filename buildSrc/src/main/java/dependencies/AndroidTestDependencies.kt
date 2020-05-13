package dependencies.dependencies

import dependencies.Versions

object AndroidTestDependencies {

    val coroutines_test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines_version}"
    val androidx_test_ext = "androidx.test.ext:junit-ktx:${Versions.androidx_test_ext}"
    val instrumentation_runner = "androidx.test.runner.AndroidJUnitRunner"
    val espresso_core = "androidx.test.espresso:espresso-core:${Versions.espresso_core}"

}