import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("org.springframework.boot") version "2.2.7.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
    kotlin("plugin.noarg") version "1.3.72"
}

application {
    //'[namespace].[arctifact]Kt'!
    mainClassName = "com.github.jonasleonhard.spring_mvc_ohm.SpringMvcOhmApplicationKt"
}
group = "com.github.JonasLeonhard"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Jackson for JSON to Class Dezerialization
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Junit Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api")

    // Db JPA HibernateORM, Postgres
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql")

    // Login Spring security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Form Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Async Flux Webclient
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Jsoup for Html text encoding / decoding & removing html tags & escaping user text
    implementation("org.jsoup:jsoup:1.13.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

// Run with gradle start
tasks.register("start") {
    this.group = "Application"
    this.description = "Run the spring_mvc_ohm backend server"
    if (project.hasProperty("build")) {
        println("[ gradle start -Pbuild=true :: Rebuilding spring_mvc_ohm ]")
        this.dependsOn(tasks.build.get())
    }
    doLast {
        if (System.getenv().contains("ENV_SPOONACULAR_API_KEY")) {
            println("INFO 00001 --- [ Run::spring_mvc_ohm -- Start ]")
            tasks.run.get().exec()
            //tasks.bootRun.get().exec()
        } else {
            val ANSI_RESET = "\u001B[0m"
            val ANSI_RED = "\u001B[31m"
            println("$ANSI_RED Startup Error: $ANSI_RESET The Environment Variable 'ENV_SPOONACULAR_API_KEY' is not set.")
            println("$ANSI_RED For Linux and MacOs type $ANSI_RESET 'export ENV_SPOONACULAR_API_KEY=[KEY]', $ANSI_RED for Windos type $ANSI_RESET 'set ENV_SPOONACULAR_API_KEY=[Key], then restart the application")
        }
    }
}