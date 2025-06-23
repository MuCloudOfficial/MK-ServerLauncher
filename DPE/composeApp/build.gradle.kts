import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}


compose.desktop {
    application {
        mainClass = "me.mucloud.application.mk.serverlauncher.dpe.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "MK-ServerLauncher Desktop Platform Edition (DPE)"
            packageVersion = "4.0.0"
            description = "Wow! Mu_Mu Here!"
            copyright = "© 2025 Mu_Cloud. All rights reserved."
            vendor = "Mu_Cloud"
            windows{
                upgradeUuid = "cdeb74ce-fbcb-470f-88f1-e665694a52d6".uppercase()
                iconFile.set(project.file("icon.ico"))
                println(project.file("icon.ico").absolutePath)
            }
        }
    }
}
