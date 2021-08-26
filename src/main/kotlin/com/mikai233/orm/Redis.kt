package com.mikai233.orm

import redis.clients.jedis.Jedis

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/26
 */

object Redis {
    val client = Jedis()
}