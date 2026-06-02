plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.13.16")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

// Configure application plugin to support multiple main classes
application {
    mainClass.set("org.example.MainKt") // Default main class
}

// Custom task to run specific main classes
tasks.register<JavaExec>("runLiftExample") {
    group = "application"
    description = "Runs the Lift Example main function"
    mainClass.set("monads.either.LiftExampleKt")
    classpath = sourceSets.main.get().runtimeClasspath
}

tasks.register<JavaExec>("runLiftAndLift2Example") {
    group = "application"
    description = "Runs the Lift and Lift2 Example main function"
    mainClass.set("monads.either.LiftAndLift2ExampleKt")
    classpath = sourceSets.main.get().runtimeClasspath
}
