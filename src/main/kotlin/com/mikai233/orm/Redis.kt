package com.mikai233.orm

import com.mikai233.tool.asyncIO
import io.ktor.config.*
import redis.clients.jedis.Jedis

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/26
 */

object Redis {
    lateinit var client: Jedis
    fun init(config: ApplicationConfig) {
        val host = config.property("redis.host").getString()
        val port = config.property("redis.port").getString().toIntOrNull() ?: 6379
        client = Jedis(host, port)
    }
}