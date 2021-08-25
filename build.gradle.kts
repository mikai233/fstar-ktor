val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val kotlinOrmVersion: String by project
plugins {
    application
    kotlin("jvm") version "1.5.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.21"
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
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("com.qiniu:qiniu-java-sdk:7.8.0")
    implementation("mysql:mysql-connector-java:8.0.26")
    implementation("redis.clients:jedis:3.6.3")
    implementation("org.ktorm:ktorm-core:$kotlinOrmVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
//    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-gson:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
}
