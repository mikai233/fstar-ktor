package com.mikai233.routes

import com.mikai233.orm.CommonResult
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/29
 */

fun Application.homeRoute() {
    routing {
        get {
            call.respond(CommonResult(data = "你好呀，欢迎访问繁星课程表后台服务，GitHub地址：https://github.com/mikai233/fstar-ktor"))
        }
    }
}