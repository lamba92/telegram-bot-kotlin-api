import com.github.gradle.node.task.NodeTask
import com.github.lamba92.kotlingram.gradle.div
import com.github.lamba92.kotlingram.gradle.outputBundleFile
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig.Mode.DEVELOPMENT
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig.Mode.PRODUCTION
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig.ResolveFallback.ModuleFallback
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig.ResolveFallback.NoFallback
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig.Target.NODE
import com.github.lamba92.kotlingram.gradle.tasks.GenerateWebpackConfig.Target.WEB
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.RootPackageJsonTask
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import java.util.*

plugins {
    kotlin("js")
    id("com.github.node-gradle.node")
}

kotlin {
    js(BOTH) {
        nodejs()
        useCommonJs()
    }
    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
        }
    }
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://kotlin.bintray.com/kotlinx")
}

dependencies {
    val ktorVersion: String by project
    val nodejsDeclarationsVersion: String by project
    val webpackVersion: String by project
    val webpackCliVersion: String by project
    implementation(project(":api:bot-builder"))
    implementation("io.ktor", "ktor-client-js", ktorVersion)
    implementation("io.ktor", "ktor-client-logging", ktorVersion)
    implementation("org.jetbrains.kotlinx", "kotlinx-nodejs", nodejsDeclarationsVersion)
    implementation(npm("webpack", webpackVersion))
    implementation(npm("webpack-cli", webpackCliVersion))
}

val rootPackageJson by rootProject.tasks.getting(RootPackageJsonTask::class)

node {
    download.set(true)
    nodeProjectDir.set(rootPackageJson.rootPackageJson.parentFile / "node_modules")
}

tasks {
    clean {
        delete("node_modules")
        delete("package-lock.json")
    }
    val compileKotlinJsLegacy: Kotlin2JsCompile by getting(Kotlin2JsCompile::class)
    val compileKotlinJsIr: Kotlin2JsCompile by getting(Kotlin2JsCompile::class)

    val generateWebpackConfigLegacy: GenerateWebpackConfig by creating(GenerateWebpackConfig::class) {
        group = "distribution"
        target = NODE
        mode = DEVELOPMENT
        entryFile = compileKotlinJsLegacy.outputFile
        modulesFolder.set(listOf(rootPackageJson.rootPackageJson.parentFile / "node_modules"))
        outputBundleName = project.name + ".js"
        outputBundleFolder = file("$buildDir/distributions").absolutePath
    }

    val webpackExecutableLegacy: NodeTask by creating(NodeTask::class) {
        group = "distribution"
        dependsOn(generateWebpackConfigLegacy, compileKotlinJsLegacy, rootPackageJson, yarn)
        script.set(rootPackageJson.rootPackageJson.parentFile / "node_modules/webpack-cli/bin/cli.js")
        args.set(listOf("-c", generateWebpackConfigLegacy.outputConfig.absolutePath))
    }

    create<NodeTask>("runWebpackExecutableLegacy") {
        group = "distribution"
        dependsOn(webpackExecutableLegacy)
        script.set(generateWebpackConfigLegacy.outputBundleFile)
        Properties().apply { load(rootProject.file("local.properties").bufferedReader()) }
            .entries.toList()
            .associate { it.key.toString() to it.value.toString() }
            .let {
                @Suppress("UnstableApiUsage")
                environment.putAll(it)
            }
    }

    val generateWebpackConfigIr: GenerateWebpackConfig by creating(GenerateWebpackConfig::class) {
        group = "distribution"
        target = NODE
        mode = DEVELOPMENT
        entryFile = compileKotlinJsIr.outputFile
        modulesFolder.set(listOf(rootPackageJson.rootPackageJson.parentFile / "node_modules"))
        outputBundleName = project.name + ".js"
        outputBundleFolder = file("$buildDir/distributions").absolutePath
    }

    val webpackExecutableIr: NodeTask by creating(NodeTask::class) {
        group = "distribution"
        dependsOn(generateWebpackConfigIr, compileKotlinJsIr, rootPackageJson, yarn)
        script.set(rootPackageJson.rootPackageJson.parentFile / "node_modules/webpack-cli/bin/cli.js")
        args.set(listOf("-c", generateWebpackConfigIr.outputConfig.absolutePath))
    }

    create<NodeTask>("runWebpackExecutableIr") {
        group = "distribution"
        dependsOn(webpackExecutableIr)
        script.set(generateWebpackConfigIr.outputBundleFile)
        Properties().apply { load(rootProject.file("local.properties").bufferedReader()) }
            .entries.toList()
            .associate { it.key.toString() to it.value.toString() }
            .let {
                @Suppress("UnstableApiUsage")
                environment.putAll(it)
            }
    }
}
