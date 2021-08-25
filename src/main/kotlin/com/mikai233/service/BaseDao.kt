package com.mikai233.service

import kotlinx.serialization.Serializable

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/22
 */

interface Data

@Serializable
data class ResultWrap(val data: Data? = null, val code: Int = 200, val message: String? = null)