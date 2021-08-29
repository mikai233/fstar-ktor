package com.mikai233.orm

import com.alibaba.druid.pool.DruidDataSource
import com.alibaba.druid.proxy.DruidDriver
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
        val dataSource = with(DruidDataSource()) {
            url = config.property("database.url").getString()
            driver = DruidDriver.getInstance().createDriver(config.property("database.driver").getString())
            username = config.property("database.user").getString()
            password = config.property("database.password").getString()
            minIdle = 1
            initialSize = 5
            maxActive = 10
            maxWait = 6000
            timeBetweenEvictionRunsMillis = 2000
            minEvictableIdleTimeMillis = 600000
            maxEvictableIdleTimeMillis = 900000
            isTestWhileIdle = true
            isTestOnBorrow = false
            isTestOnReturn = false
            init()
            this
        }
        database =
            Database.connect(dataSource = dataSource, dialect = MySqlDialect(), logger = Slf4jLoggerAdapter(logger))
    }

    val devices get() = database.sequenceOf(Devices)
    val users get() = database.sequenceOf(Users)
    val versions get() = database.sequenceOf(Versions)
    val scores get() = database.sequenceOf(Scores)
    val messages get() = database.sequenceOf(Messages)
    val parseConfigs get() = database.sequenceOf(ParseConfigs)
}