import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  id("org.graalvm.buildtools.native") version "0.9.11"
  id("org.springframework.boot") version "2.7.0"
  id("org.springframework.experimental.aot") version "0.12.0"
  kotlin("jvm") version "1.7.0"
  kotlin("kapt") version "1.7.0"
  kotlin("plugin.jpa") version "1.7.0"
  kotlin("plugin.spring") version "1.7.0"
}

group = "io.nvtc"

version = "0.0.1-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_17

dependencies {
  kapt("org.hibernate:hibernate-jpamodelgen")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.flywaydb:flyway-core:8.5.13")
  implementation("org.flywaydb:flyway-mysql:8.5.13")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.mariadb.jdbc:mariadb-java-client:3.0.5")
  implementation("org.postgresql:postgresql")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.experimental:spring-native:0.12.0")
  compileOnly("org.graalvm.nativeimage:svm:22.1.0.1")
  testImplementation("com.h2database:h2")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
}

nativeBuild {
  buildArgs.set(
      listOf(
          "--initialize-at-build-time=io.nvtc.csc.jpa.NativePathLocationScanner",
          "-Dflyway.locations=classpath:db/migration/postgres"))
}

repositories {
  maven { url = uri("https://repo.spring.io/release") }
  mavenCentral()
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "17"
  }
}

tasks.withType<Test> { useJUnitPlatform() }
