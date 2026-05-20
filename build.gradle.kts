import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "org.example.project.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.project"
            packageVersion = "1.0.0"
        }
    }
}

tasks.register<JavaExec>("runServer") {
    group = "application"
    description = "Run TCP chat server"
    dependsOn("compileKotlinJvm")
    mainClass.set("org.example.project.ServerKt")
    val kotlinStdlib = dependencies.create("org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.get()}")
    classpath(
        files(kotlin.targets.getByName("jvm").compilations.getByName("main").output.allOutputs),
        configurations.detachedConfiguration(kotlinStdlib)
    )
}
