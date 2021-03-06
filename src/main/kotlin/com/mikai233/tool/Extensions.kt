@file:Suppress("unused")

package com.mikai233.tool

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.mikai233.orm.DB
import com.mikai233.orm.Redis
import io.ktor.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis
import java.util.*
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/22
 */

const val Millisecond = 1L
const val Seconds = Millisecond * 1000L
const val Minutes = Seconds * 60L
const val Hours = Minutes * 60L
const val Days = Hours * 24L

fun Application.property(path: String) = environment.config.property(path).getString()

fun Application.properties(path: String) = environment.config.property(path).getList()

fun Application.propertyOrNull(path: String) = environment.config.propertyOrNull(path)?.getString()

fun Application.propertiesOrNull(path: String) = environment.config.propertyOrNull(path)?.getList()

fun Application.generateToken(username: String): Map<String, String> {
    val audience = property("jwt.audience")
    val issuer = property("jwt.issuer")
    val secret = property("jwt.secret")
    val token = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("username", username)
        .withExpiresAt(Date(System.currentTimeMillis() + 10 * Minutes))
        .sign(Algorithm.HMAC256(secret))
    return hashMapOf(
        "token" to token,
        "tokenHeader" to property("jwt.tokenHeader"),
        "tokenPrefix" to property("jwt.tokenPrefix")
    )
}

fun exceptionHandler(context: CoroutineContext, throwable: Throwable) {
    val coroutineLogger = context[CoroutineLogger]
    coroutineLogger?.logger?.error("{}", context, throwable)
}

class CoroutineLogger : AbstractCoroutineContextElement(Key) {
    val logger: Logger = LoggerFactory.getLogger(javaClass)

    companion object Key : CoroutineContext.Key<CoroutineLogger>
}

/**
 * ???IO??????????????????????????????
 * ????????????[CoroutineLogger]
 */
suspend fun <T> DB.asyncIO(context: CoroutineContext = EmptyCoroutineContext, block: suspend DB.() -> T): T {
    return withContext(Dispatchers.IO + CoroutineLogger() + context) {
        block()
    }
}

/**
 * ???IO???????????????Redis???????????????
 * ????????????[CoroutineLogger]
 */
suspend fun <T> Redis.asyncIO(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend Redis.(jedis: Jedis) -> T
): T {
    return withContext(Dispatchers.IO + CoroutineLogger() + context) {
        jedisPool.resource.use {
            block(it)
        }
    }
}

fun String.camelCase(): String {
    return split("_").mapIndexed { index, value ->
        if (value.isEmpty()) {
            ""
        } else if (index != 0) {
            val charArray = value.toCharArray()
            charArray[0] = value[0].uppercaseChar()
            charArray.joinToString("")
        } else value
    }.joinToString("")
}