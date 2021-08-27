@file:Suppress("unused")

package com.mikai233.tool

import com.mikai233.orm.DB
import io.ktor.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/22
 */

const val Millisecond = 1000
const val Seconds = Millisecond * 10
const val Minutes = Seconds * 60
const val Hours = Minutes * 60
const val Days = Hours * 24

fun Application.property(path: String) = environment.config.property(path).getString()

fun Application.properties(path: String) = environment.config.property(path).getList()

fun Application.propertyOrNull(path: String) = environment.config.propertyOrNull(path)?.getString()

fun Application.propertiesOrNull(path: String) = environment.config.propertyOrNull(path)?.getList()

//@OptIn(ExperimentalContracts::class)
//fun <T : Any> PipelineContext<*, ApplicationCall>.notNull(value: T?, lazyMessage: () -> Any): T {
//    contract {
//        returns() implies (value != null)
//    }
//    if (value == null) {
//        val message = lazyMessage()
//
//    } else {
//        return value
//    }
//}

/**
 * 在IO线程中操作数据库读写
 */
suspend fun <T> DB.asyncIO(block: DB.() -> T): T {
    return withContext(Dispatchers.IO) {
        block()
    }
}