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

fun Application.property(path: String) = environment.config.property(path).getString()

fun Application.properties(path: String) = environment.config.property(path).getList()

fun Application.propertyOrNull(path: String) = environment.config.propertyOrNull(path)?.getString()

fun Application.propertiesOrNull(path: String) = environment.config.propertyOrNull(path)?.getList()

/**
 * 在IO线程中操作数据库读写
 */
suspend fun <T> DB.asyncIO(block: DB.() -> T): T {
    return withContext(Dispatchers.IO) {
        block()
    }
}