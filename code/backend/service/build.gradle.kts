plugins {
    kotlin("jvm") version "1.9.25"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("com.adarshr.test-logger") version "4.0.0"
}

group = "pt.rs"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":domain"))
    api(project(":repository"))

    // To get the DI annotation
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

    // To use Kotlin specific date and time functions
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

    // To use SLF4J
    implementation("org.slf4j:slf4j-api:2.0.16")

    // To use on Google Drive API Oauth2
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0") // Google Auth Library
    implementation("com.google.apis:google-api-services-drive:v3-rev20220815-2.0.0") // Google Drive API
    implementation("org.springframework.boot:spring-boot-starter-web:3.4.0") // Spring Web

    // for JDBI
    testImplementation(project(":repository"))
    testImplementation("org.jdbi:jdbi3-core:3.37.1")
    testImplementation("org.postgresql:postgresql:42.7.2")


    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
    environment("DB_URL", "jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
    dependsOn(":repository:dbTestsWait")
    finalizedBy(":repository:dbTestsDown")
}

kotlin {
    jvmToolchain(21)
}