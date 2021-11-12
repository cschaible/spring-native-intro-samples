import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("org.graalvm.buildtools.native") version "0.9.4"
	id("org.springframework.boot") version "2.5.6"
	id("org.springframework.experimental.aot") version "0.10.5"
	kotlin("jvm") version "1.5.31"
	kotlin("kapt") version "1.5.31"
	kotlin("plugin.jpa") version "1.5.31"
	kotlin("plugin.spring") version "1.5.31"
}

group = "io.nvtc"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

dependencies {
	kapt("org.hibernate:hibernate-jpamodelgen")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.mariadb.jdbc:mariadb-java-client:2.7.4")
	implementation("org.postgresql:postgresql")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.experimental:spring-native:0.10.5")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

nativeBuild {
	classpath(tasks.named("processAotResources").get().outputs, tasks.named("compileAotJava").get().outputs)
}

repositories {
	maven { url = uri("https://repo.spring.io/release") }
	mavenCentral()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
