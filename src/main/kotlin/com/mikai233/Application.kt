package com.mikai233

import com.mikai233.orm.DB
import com.mikai233.plugins.configureHTTP
import com.mikai233.plugins.configureRouting
import com.mikai233.plugins.configureSecurity
import com.mikai233.plugins.configureSerialization
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, environment = applicationEngineEnvironment {
        val commonConfig = ConfigFactory.load()
        val env = commonConfig.getString("ktor.environment")
        config = when (env) {
            "dev" -> HoconApplicationConfig(
                ConfigFactory.load("application_dev.conf").withFallback(commonConfig)
            )
            "prod" -> HoconApplicationConfig(ConfigFactory.load("application_prod.conf").withFallback(commonConfig))
            else -> throw IllegalArgumentException("Unsupported mode $env.")
        }
        DB.init(config)
        connector {
            host = config.property("ktor.deployment.host").getString()
            port = config.property("ktor.deployment.port").getString().toInt()
        }
    }) {
    }.start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureRouting()
    configureHTTP()
    configureSerialization()
}
