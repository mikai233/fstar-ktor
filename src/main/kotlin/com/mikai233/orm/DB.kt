package com.mikai233.orm

import io.ktor.config.*
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.logging.Slf4jLoggerAdapter
import org.ktorm.support.mysql.MySqlDialect
import org.slf4j.LoggerFactory

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/22
 */

object DB {
    lateinit var database: Database
    private val logger = LoggerFactory.getLogger(javaClass)

    fun init(config: ApplicationConfig) {
        database = Database.connect(
            url = config.property("database.url").getString(),
            driver = config.property("database.driver").getString(),
            user = config.property("database.user").getString(),
            password = config.property("database.password").getString(),
            dialect = MySqlDialect(),
            logger = Slf4jLoggerAdapter(logger)
        )
    }

    val devices get() = database.sequenceOf(Devices)
    val users get() = database.sequenceOf(Users)
    val versions get() = database.sequenceOf(Versions)
    val scores get() = database.sequenceOf(Scores)
    val messages get() = database.sequenceOf(Messages)
    val parseConfigs get() = database.sequenceOf(ParseConfigs)
}