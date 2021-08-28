package com.mikai233

import com.mikai233.orm.Redis
import com.mikai233.service.redisService
import com.typesafe.config.ConfigFactory
import io.ktor.config.*
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/28
 */

class RedisServiceTest {
    @Before
    fun loadConfig() {
        val commonConfig = ConfigFactory.load()
        val config = HoconApplicationConfig(
            ConfigFactory.load("application_dev.conf").withFallback(commonConfig)
        )
        Redis.init(config)
    }

    @Test
    fun testCurrentDayVitality(): Unit = runBlocking {
        redisService.currentDayVitality().forEach {
            println(it)
        }
    }

    @Test
    fun testCurrentWeekVitality(): Unit = runBlocking {
        redisService.currentWeekVitality()
    }

    @Test
    fun testCurrentMonthVitality(): Unit = runBlocking {
        redisService.currentMonthVitality()
    }

    @Test
    fun testDayVitality(): Unit = runBlocking {
        redisService.dayVitality(LocalDate.now()).forEach {
            println(it)
        }
    }

    @Test
    fun testWeekVitality(): Unit = runBlocking {
        redisService.weekVitality(LocalDate.now()).forEach {
            println(it)
        }
    }

    @Test
    fun testMonthVitality(): Unit = runBlocking {
        redisService.monthVitality(LocalDate.now()).forEach {
            println(it)
        }
    }
}