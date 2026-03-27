import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "2.3.10"
    kotlin("plugin.power-assert") version "2.3.10"
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.spring") version "2.3.20"
}

group = "com.conference"
version = "0.0.1-SNAPSHOT"
val testcontainersVersion = "2.0.4"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.projectreactor:reactor-core")
    implementation("tools.jackson.module:jackson-module-kotlin:3.0.4")
    compileOnly("org.jspecify:jspecify:1.0.0")

    runtimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("io.kotest:kotest-assertions-core-jvm:6.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.10.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("com.ninja-squad:springmockk:5.0.1")
    testImplementation(kotlin("test-junit5"))
    implementation(kotlin("stdlib"))
}

val integrationTest by sourceSets.creating {
    java.srcDirs("src/integrationTest/java", "src/integrationTest/kotlin")

    compileClasspath += sourceSets["main"].output + sourceSets["test"].output + configurations["testRuntimeClasspath"]
    runtimeClasspath += output + compileClasspath
}

configurations[integrationTest.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())
configurations[integrationTest.runtimeOnlyConfigurationName].extendsFrom(configurations.testRuntimeOnly.get())

dependencies {
    add(integrationTest.implementationConfigurationName, "org.testcontainers:testcontainers-postgresql:$testcontainersVersion")
    add(integrationTest.runtimeOnlyConfigurationName, "org.testcontainers:testcontainers-jdbc:$testcontainersVersion")
    add(integrationTest.runtimeOnlyConfigurationName, "org.postgresql:postgresql")
}

powerAssert {
    functions = listOf(
        "kotlin.assert",
        "kotlin.test.assertEquals",
        "kotlin.test.assertNotNull",
        "kotlin.test.assertTrue",
        "kotlin.test.assertContentEquals",
        "io.kotest.matchers.shouldBe",
    )
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("failed")
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

val integrationTestTask = tasks.register<Test>("integrationTest") {
    description = "Runs integration tests"
    group = "verification"
    testClassesDirs = integrationTest.output.classesDirs
    classpath = integrationTest.runtimeClasspath
    shouldRunAfter(tasks.test)
    systemProperty("spring.profiles.active", "integrationtest")

    useJUnitPlatform()
    testLogging {
        events("failed")
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

tasks.named("check") {
    dependsOn(integrationTestTask)
}

tasks.named<JavaCompile>("compileTestJava") {
    options.release = 21
}

tasks.named<JavaCompile>("compileIntegrationTestJava") {
    options.release = 21
}

tasks.withType<KotlinJvmCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}
