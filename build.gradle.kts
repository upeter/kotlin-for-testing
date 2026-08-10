import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    kotlin("jvm") version "2.3.20"
    kotlin("plugin.spring") version "2.3.20"
    kotlin("plugin.jpa") version "2.3.20"
    kotlin("plugin.power-assert") version "2.3.20"
    id("org.springframework.boot") version "4.0.3"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("com.autonomousapps.dependency-analysis") version "2.7.0"

}

group = "com.conference"
version = "0.0.1-SNAPSHOT"
description = "Java-first Spring Boot conference website"

val springBootVersion = "4.0.3"
val kotlinVersion = "2.3.20"
val testcontainersVersion = "2.0.4"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
        freeCompilerArgs.add("-Xcontext-parameters")
        // Strict compilation: every compiler warning is a build error.
        // This is the LSP-equivalent guardrail (mirrors javac -Werror).
        allWarningsAsErrors = true
    }
}

powerAssert {
    functions = setOf(
        "kotlin.assert",
        "kotlin.test.assertEquals",
        "kotlin.test.assertNotNull",
        "kotlin.test.assertTrue",
        "kotlin.test.assertContentEquals",
    )
}

repositories {
    mavenCentral()
    maven {
        // JetBrains TeamCity repository (kept in parity with pom.xml)
        url = uri("https://packages.jetbrains.team")
    }
}

// integration tests live in their own source set, mirroring src/integrationTest in the pom.
// The default src/integrationTest/{kotlin,java,resources} conventions apply automatically.
val integrationTest =
    sourceSets.create("integrationTest") {
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    }

configurations["integrationTestImplementation"].extendsFrom(configurations["testImplementation"])
configurations["integrationTestRuntimeOnly"].extendsFrom(configurations["testRuntimeOnly"])

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    testImplementation(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.27.3")

    // Konsist — architecture constraint tests
    testImplementation("com.lemonappdev:konsist:0.17.3")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.projectreactor:reactor-core")
    compileOnly("org.jspecify:jspecify:1.0.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("tools.jackson.module:jackson-module-kotlin:3.0.4")
    runtimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.kotest:kotest-assertions-core-jvm:6.1.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.10.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("com.ninja-squad:springmockk:5.0.1")
    testImplementation("org.testcontainers:testcontainers-postgresql:$testcontainersVersion")
    testImplementation("org.testcontainers:testcontainers-jdbc:$testcontainersVersion")
    testImplementation("org.postgresql:postgresql")
}

tasks.test {
    useJUnitPlatform()
}

// mirrors the maven-failsafe-plugin binding: *IT classes run under the "integrationtest" profile
val integrationTestTask =
    tasks.register<Test>("integrationTest") {
        description = "Runs integration tests (*IT classes) against the integrationtest profile."
        group = "verification"
        testClassesDirs = integrationTest.output.classesDirs
        classpath = integrationTest.runtimeClasspath
        useJUnitPlatform()
        include("**/*IT.class")
        systemProperty("spring.profiles.active", "integrationtest")
        shouldRunAfter(tasks.test)
    }

tasks.check {
    dependsOn(integrationTestTask)
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("config/detekt/detekt.yml"))
    basePath = projectDir.path
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required = true
        xml.required = false
        txt.required = false
        sarif.required = false
    }
}

// --- ktlint (formatting and style, including wildcard imports) ---
ktlint {
    version = "1.5.0"
    ignoreFailures = false
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
    }
}

// --- Kover (code coverage) ---
kover {
    reports {
        verify {
            rule {
                minBound(80)
            }
        }
    }
}

// --- Dependency locking (for vulnerability scanning with trivy) ---
configurations {
    compileClasspath { resolutionStrategy.activateDependencyLocking() }
    runtimeClasspath { resolutionStrategy.activateDependencyLocking() }
    testCompileClasspath { resolutionStrategy.activateDependencyLocking() }
    testRuntimeClasspath { resolutionStrategy.activateDependencyLocking() }
}

// --- Dependency analysis (unused / undeclared dependency hygiene) ---
dependencyAnalysis {
    issues {
        all {
            onAny {
                severity("fail")
            }
        }
    }
}


