import org.jetbrains.kotlin.com.google.common.collect.ImmutableMap
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.1.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	id("com.palantir.docker") version "0.25.0"
	id("com.palantir.docker-run") version "0.25.0"
	kotlin("jvm") version "1.3.72"
	kotlin("plugin.spring") version "1.3.72"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("io.springfox:springfox-boot-starter:3.0.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("com.nimbusds:nimbus-jose-jwt:8.19")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("io.r2dbc:r2dbc-postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
		exclude(module = "mockito-core")
	}
	//testImplementation("org.springframework.boot:spring-security-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("com.ninja-squad:springmockk:2.0.2")
	testImplementation("io.mockk:mockk:1.10.0")
	testRuntimeOnly("com.h2database:h2")
	testRuntimeOnly("io.r2dbc:r2dbc-h2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

apply(plugin = "com.palantir.docker")

docker {
	name = "owahlen/task-manager:".plus(version)
	uri("owahlen/task-manager:".plus(version))
	tag("name", "task-manager")
	buildArgs(mapOf("NAME" to "task-manager", "BUILD_VERSION" to version.toString()))
	copySpec.from("build").into("build")
	pull(true)
	setDockerfile(file("Dockerfile"))
}

dockerRun {
	name = "task-manager"
	image = "owahlen/task-manager:".plus(version)
	ports("8080:8080")
}
