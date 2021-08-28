package com.mikai233.plugins

import com.mikai233.orm.DB
import com.mikai233.orm.Redis
import com.mikai233.tool.CoroutineLogger
import com.mikai233.tool.exceptionHandler
import io.ktor.application.*
import kotlinx.coroutines.*

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/28
 */
//TODO 有问题
@OptIn(DelicateCoroutinesApi::class)
fun Application.configureDB() {
    GlobalScope.launch(Dispatchers.IO + CoroutineLogger()) {
        supervisorScope {
            launch(CoroutineExceptionHandler(::exceptionHandler)) {
                DB.init(environment.config)
            }
            launch(CoroutineExceptionHandler(::exceptionHandler)) {
                Redis.init(environment.config)
            }
        }
    }
}