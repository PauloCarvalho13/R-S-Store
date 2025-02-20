plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "pt.rs"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":http-api"))
    implementation(project(":repository"))
    implementation(project(":http-pipeline"))


    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

    // for JDBI and Postgres
    implementation("org.jdbi:jdbi3-core:3.37.1")
    implementation("org.postgresql:postgresql:42.7.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
    useJUnitPlatform()
    environment("DB_URL", "jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
    dependsOn(":repository:dbTestsWait")
    finalizedBy(":repository:dbTestsDown")
}
kotlin {
    jvmToolchain(21)
}

/**
 * Docker related tasks
 */

task<Copy>("extractUberJar") {
    dependsOn("assemble")
    // opens the JAR containing everything...
    from(zipTree(layout.buildDirectory.file("libs/lesson15-host-$version.jar").get().toString()))
    // ... into the 'build/dependency' folder
    into("build/dependency")
}
val dockerImageJvm = "rs-catalog-jvm"
val dockerImageNginx = "rs-catalog-nginx"
val dockerImagePostgresTest = "rs-catalog-postgres-test"

task<Exec>("buildImageJvm") {
    dependsOn("extractUberJar")
    commandLine("docker", "build", "-t", dockerImageJvm, "-f", "infrastructure/Dockerfile-jvm", ".")
}

task<Exec>("buildImageNginx") {
    commandLine("docker", "build", "-t", dockerImageNginx, "-f", "infrastructure/Dockerfile-nginx", ".")
}

task<Exec>("buildImagePostgresTest") {
    commandLine(
        "docker",
        "build",
        "-t",
        dockerImagePostgresTest,
        "-f",
        "infrastructure/Dockerfile-postgres-test",
        "../repository",
    )
}

task("buildImageAll") {
    dependsOn("buildImageJvm")
    dependsOn("buildImageNginx")
    dependsOn("buildImagePostgresTest")
}

task<Exec>("allUp") {
    commandLine("docker", "compose", "up", "--no-recreate", "-d")
}

task<Exec>("allDown") {
    commandLine("docker", "compose", "down")
}