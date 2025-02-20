plugins {
    kotlin("jvm") version "1.9.25"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "pt.rs"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))

    implementation("jakarta.inject:jakarta.inject-api:2.0.1")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

    implementation("org.slf4j:slf4j-api:2.0.16")

    // for JDBI
    implementation("org.jdbi:jdbi3-core:3.37.1")
    implementation("org.jdbi:jdbi3-kotlin:3.37.1")
    implementation("org.jdbi:jdbi3-postgres:3.37.1")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

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

/**
 * DB related tasks
 * - To run `psql` inside the container, do
 *      docker exec -ti db-tests psql -d db -U dbuser -W
 *   and provide it with the same password as define on `tests/Dockerfile-db-test`
 */

val composeFileDir: Directory = rootProject.layout.projectDirectory
val dockerComposePath = composeFileDir.file("docker-compose.yml").toString()

task<Exec>("dbTestsUp") {
    commandLine("docker", "compose", "-f", dockerComposePath, "up", "-d", "--build", "--force-recreate", "db-tests")
}

task<Exec>("dbTestsWait") {
    commandLine("docker", "exec", "db-tests", "/app/bin/wait-for-postgres.sh", "localhost")
    dependsOn("dbTestsUp")
}

task<Exec>("dbTestsDown") {
    commandLine("docker", "compose", "-f", dockerComposePath, "down", "db-tests")
}