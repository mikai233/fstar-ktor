package com.mikai233

import com.mikai233.plugins.configureDB
import com.mikai233.plugins.configureRouting
import com.mikai233.plugins.configureSecurity
import com.mikai233.plugins.configureSerialization
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory
import java.security.KeyStore

fun main() {
    val logger = LoggerFactory.getLogger("Application")
    embeddedServer(Netty, environment = applicationEngineEnvironment {
        val commonConfig = ConfigFactory.load()
        val env = commonConfig.getString("ktor.environment")
        logger.info("Note: You are using $env conf.")
        config = when (env) {
            "dev" -> HoconApplicationConfig(
                ConfigFactory.load("application_dev.conf").withFallback(commonConfig)
            )
            "prod" -> HoconApplicationConfig(ConfigFactory.load("application_prod.conf").withFallback(commonConfig))
            else -> throw IllegalArgumentException("Unsupported mode $env.")
        }
        val useSSL = config.property("ktor.useSSL").getString().toBoolean()
        val hostConfig = config.property("ktor.deployment.host").getString()
        val portConfig = config.property("ktor.deployment.port").getString().toInt()
        if (useSSL) {
            val keyStoreFileInputStream =
                ClassLoader.getSystemClassLoader()
                    .getResourceAsStream(config.property("ktor.security.ssl.keyStore").getString())
            val password = config.property("ktor.security.ssl.keyStorePassword").getString().toCharArray()
            val keyStore = KeyStore.getInstance("JKS").also {
                it.load(keyStoreFileInputStream, password)
            }
            sslConnector(
                keyStore = keyStore,
                keyAlias = config.property("ktor.security.ssl.keyAlias").getString(),
                keyStorePassword = { config.property("ktor.security.ssl.keyStorePassword").getString().toCharArray() },
                privateKeyPassword = {
                    config.property("ktor.security.ssl.privateKeyPassword").getString().toCharArray()
                }
            ) {
                host = hostConfig
                port = portConfig
            }
        } else {
            connector {
                host = hostConfig
                port = portConfig
            }
        }
    }) {
    }.start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureRouting()
//    configureHTTP()
    configureSerialization()
    configureDB()
}
