plugins {
    kotlin("jvm") version "1.9.22"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.firebase:firebase-admin:9.2.0")
    implementation ("org.slf4j:slf4j-api:1.7.32")
	implementation ("ch.qos.logback:logback-classic:1.2.6")
    implementation ("org.slf4j:slf4j-nop:2.0.7")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation("org.json:json:20240303")
    implementation("org.apache.commons:commons-csv:1.10.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}