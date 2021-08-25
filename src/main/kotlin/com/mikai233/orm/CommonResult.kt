package com.mikai233.orm

import io.ktor.http.*

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/25
 */

/**
 * 统一返回结果
 * @param httpStatusCode 正常都返回[HttpStatusCode.OK] 其余根据需要返回不同的类型
 * @param data 任意类型的消息体
 */
data class CommonResult(val httpStatusCode: HttpStatusCode = HttpStatusCode.OK, val data: Any? = null)
