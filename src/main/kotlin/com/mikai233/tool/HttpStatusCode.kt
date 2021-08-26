package com.mikai233.tool

import io.ktor.http.*

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/25
 */

val userNotFound = HttpStatusCode(4001, "用户名不存在")

val passwordIncorrect = HttpStatusCode(4002, "密码不正确")

val requestParamInvalid = HttpStatusCode(4003, "请求参数不合法")