package com.mikai233.orm

import io.ktor.config.*
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig


/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/26
 */

object Redis {
    private val poolConfig = JedisPoolConfig()
    lateinit var jedisPool: JedisPool
    fun init(config: ApplicationConfig) {
        val host = config.property("redis.host").getString()
        val port = config.property("redis.port").getString().toIntOrNull() ?: 6379
        jedisPool = JedisPool(poolConfig, host, port)
    }
}