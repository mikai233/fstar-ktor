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

/**
 * 兼容老客户端的统一返回值
 * @param data 任意类型的返回结果
 * @param message 提示消息
 * @param code 状态码
 */
data class OldCommonResult(val data: Any? = null, val message: String = "", val code: Int = HttpStatusCode.OK.value)
