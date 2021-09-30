package com.mikai233.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.cookies.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.jsoup.Jsoup
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * @author mikai233(dairch)
 * @date 2021/9/27
 */

suspend fun HttpClient.login(username: String, password: String): HttpResponse {
    return submitForm<HttpResponse>(
        formParameters = parametersOf("USERNAME" to listOf(username), "PASSWORD" to listOf(password))
    ) {
        url.takeFrom(LOGIN_URL)
    }.also { httpResponse ->
        val fullPath = httpResponse.request.url.fullPath
        if (fullPath != "/jsxsd/framework/xsMain.jsp" && fullPath != "/jsxsd/grsz/grsz_xgmm_beg.do") {
            throw Exception("账号或密码不正确[username:$username][password:$password]")
        }
    }
}

suspend inline fun <reified T> HttpClient.logout(): T {
    return submitForm<T>(
        encodeInQuery = true,
        formParameters = parametersOf(
            "method" to listOf("exit"), "tktime" to listOf(
                LocalDateTime.now().toEpochSecond(
                    ZoneOffset.UTC
                ).toString()
            )
        )
    ) {
        url.takeFrom(LOGIN_URL)
    }.also {
        close()
    }
}

suspend inline fun <reified T> HttpClient.getScore(semester: String = "", display: String = "ALL"): T {
    return submitForm(
        formParameters = parametersOf(
            "kksj" to listOf(semester), "xsfs" to listOf(display)
        )
    ) {
        url.takeFrom(SCORE_URL)
    }
}

suspend inline fun <reified T> HttpClient.getScore2(semester: String = ""): T {
    return submitForm(
        formParameters = parametersOf(
            "xnxq01id" to listOf(semester)
        )
    ) {
        url.takeFrom(SCORE2_URL)
    }
}

suspend inline fun <reified T> HttpClient.getCourse(semester: String = ""): T {
    return submitForm(
        formParameters = parametersOf(
            "xnxq01id" to listOf(semester)
        )
    ) {
        url.takeFrom(COURSE_URL)
    }
}

suspend fun HttpClient.sportLogin(username: String, password: String): HttpResponse {
    return submitForm<HttpResponse>(
        formParameters = parametersOf(
            "username" to listOf(username),
            "password" to listOf(password),
            "chkuser" to listOf("true")
        )
    ) {
        url.takeFrom(SPORT_LOGIN_URL)
    }.also { httpResponse ->
        //gbk解码
        val str = httpResponse.receive<String>()
        val document = Jsoup.parse(str)
        val info = document.select("#autonumber2 p")
        if ("密码或用户名不正确，请返回重输！" == info.text()) {
            throw Exception("体育账号或密码不正确")
        }
    }
}

suspend inline fun <reified T> HttpClient.getSportScore(): T {
    return get(SPORT_SCORE_URL)
}

suspend inline fun <reified T> HttpClient.getSportMorning(): T {
    return get(SPORT_MORNING_URL)
}

suspend inline fun <reified T> HttpClient.getSportClub(): T {
    return get(SPORT_CLUB_URL)
}

fun ktorClient() = HttpClient(CIO) {
    expectSuccess = false
    BrowserUserAgent()
    install(HttpCookies) {
        storage = AcceptAllCookiesStorage()
    }
    install(HttpRedirect) {
        checkHttpMethod = false
    }
//    engine {
//        proxy = ProxyBuilder.http("http://localhost:8866")
//    }
}

suspend fun main() {
    val client = ktorClient()
    client.login(USERNAME, PASSWORD)
    client.getScore<String>("2019-2020-2").also {
        it.parseScore().forEach {
            println(it)
        }
    }
    client.logout<String>()
}