package com.mikai233.client

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * @author mikai233(dairch)
 * @date 2021/9/28
 */

suspend fun main() {
    with(ktorClient()) {
        val response = vpn2loginPage<HttpResponse>()
        val loginParameters = response.parseVpn2LoginParameter()
        vpn2Login<String>(VPN_USERNAME, VPN_PASSWORD, loginParameters = loginParameters).also {
//            println(it)
        }
//        get<String>("https://client.v.just.edu.cn/http/webvpneb26120c0b61d26f61ce45ea5ef07bf864a455884ca2133c138748630669de2c/sso.jsp").also {
//            println(it)
//        }
        get<String>("https://client.v.just.edu.cn/http/webvpn1aaa8d8c3150b7106ebc332d79d6f247b58614307c31e1683a3314b9ce47e29a/_s4/2019/1210/c131a45300/page.psp").also {
            println(it)
        }
    }
}

suspend inline fun <reified T> HttpClient.vpn2loginPage(): T {
    return get(VPN2_LOGIN_URL)
}

suspend inline fun <reified T> HttpClient.vpn2Login(
    username: String,
    password: String,
    code: String = "",
    loginParameters: HashMap<String, String> = hashMapOf()
): T {
    loginParameters["username"] = username
    loginParameters["password"] = password
    val loginUrl = requireNotNull(loginParameters.remove("loginUrl"))
    val formParameters = loginParameters.map { it.key to listOf(it.value) }
    val arrayOfPairs = Array(formParameters.size) { index -> formParameters[index] }
    val response = submitForm<HttpResponse>(
        formParameters = parametersOf(
            *arrayOfPairs
        )
    ) {
        url.takeFrom(loginUrl)
    }
    return if (response.status == HttpStatusCode.BadRequest) {
        get("${get<HttpResponse>(response.request.url).request.url}_s2/students_sy/main.psp")
    } else {
        throw Exception("账号或密码不正确")
    }
}

suspend inline fun <reified T> HttpClient.vpn2GetCourse() {

}