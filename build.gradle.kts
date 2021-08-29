import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val kotlinOrmVersion: String by project
val javaVersion = JavaVersion.VERSION_11.toString()

plugins {
    application
    kotlin("jvm") version "1.5.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.mikai233"
version = "0.0.1"
application {
    mainClass.set("com.mikai233.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.bouncycastle:bcprov-jdk15on:1.69")
    implementation("commons-logging:commons-logging:1.2")
    implementation("org.springframework.security:spring-security-crypto:5.5.2")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("com.qiniu:qiniu-java-sdk:7.8.0")
    implementation("mysql:mysql-connector-java:8.0.26")
    implementation("com.alibaba:druid:1.2.6")
    implementation("redis.clients:jedis:3.6.3")
    implementation("org.ktorm:ktorm-core:$kotlinOrmVersion")
    implementation("org.ktorm:ktorm-support-mysql:$kotlinOrmVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-gson:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = javaVersion
}

tasks.withType<JavaCompile> {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks.register("generateDockerfile") {
    dependsOn(tasks.getByName("shadowJar"))
    val dockerCommand = """
        FROM openjdk:11
        COPY build/libs/fstar-ktor-$version-all.jar fstar-ktor.jar
        EXPOSE 9009
        ENTRYPOINT ["java","-jar","-Ddruid.mysql.usePingMethod=false","/fstar-ktor.jar"]
    """.trimIndent()
    File("Dockerfile").writeText(dockerCommand)
}