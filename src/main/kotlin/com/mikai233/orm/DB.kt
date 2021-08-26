package com.mikai233.orm

import io.ktor.config.*
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import org.ktorm.support.mysql.MySqlDialect

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/22
 */

object DB {
    lateinit var database: Database

    fun init(config: ApplicationConfig) {
        database = Database.connect(
            url = config.property("database.url").getString(),
            driver = config.property("database.driver").getString(),
            user = config.property("database.user").getString(),
            password = config.property("database.password").getString(),
            dialect = MySqlDialect(),
            logger = ConsoleLogger(threshold = LogLevel.INFO)
        )
    }

    val devices get() = database.sequenceOf(Devices)
    val users get() = database.sequenceOf(Users)
    val versions get() = database.sequenceOf(Versions)
}