package com.mikai233.routes

import com.mikai233.client.*
import com.mikai233.orm.CommonResult
import com.mikai233.tool.requestParamInvalid
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * @author mikai233(dairch)
 * @date 2021/9/28
 */

fun Application.uniAppRoute() {
    routing {
        route("/cloud") {
            route("/just") {
                get("/course") {
                    val queryParameters = call.request.queryParameters
                    val username = queryParameters["username"]
                    val password = queryParameters["password"]
                    val semester = queryParameters["semester"]
                    if (username == null || password == null || semester == null) {
                        call.respond(CommonResult(requestParamInvalid))
                        return@get
                    }
                    try {
                        with(ktorClient()) {
                            login(username, password)
                            getCourse<String>(semester).parseCourse().let {
                                call.respond(CommonResult(data = mapOf("courses" to it.first, "remark" to it.second)))
                            }
                            logout()
                        }
                    } catch (e: Exception) {
                        call.respond(CommonResult(HttpStatusCode.InternalServerError, "${e.javaClass} ${e.message}"))
                    }
                }
                get("/score") {
                    val queryParameters = call.request.queryParameters
                    val username = queryParameters["username"]
                    val password = queryParameters["password"]
                    val semester = queryParameters["semester"] ?: ""
                    val display = queryParameters["display"] ?: "ALL"
                    if (username == null || password == null) {
                        call.respond(CommonResult(requestParamInvalid))
                        return@get
                    }
                    try {
                        with(ktorClient()) {
                            login(username, password)
                            getScore<String>(semester).parseScore().let {
                                call.respond(CommonResult(data = mapOf("scores" to it)))
                            }
                            logout()
                        }
                    } catch (e: Exception) {
                        call.respond(CommonResult(HttpStatusCode.InternalServerError, "${e.javaClass} ${e.message}"))
                    }
                }
                get("/score2") {
                    val queryParameters = call.request.queryParameters
                    val username = queryParameters["username"]
                    val password = queryParameters["password"]
                    val semester = queryParameters["semester"] ?: ""
                    if (username == null || password == null) {
                        call.respond(CommonResult(requestParamInvalid))
                        return@get
                    }
                    try {
                        with(ktorClient()) {
                            login(username, password)
                            getScore2<String>(semester).parseScore(true).let {
                                call.respond(CommonResult(data = mapOf("scores" to it)))
                            }
                            logout()
                        }
                    } catch (e: Exception) {
                        call.respond(CommonResult(HttpStatusCode.InternalServerError, "${e.javaClass} ${e.message}"))
                    }
                }
                get("/sport_score") {
                    val queryParameters = call.request.queryParameters
                    val username = queryParameters["username"]
                    val password = queryParameters["password"]
                    if (username == null || password == null) {
                        call.respond(CommonResult(requestParamInvalid))
                        return@get
                    }
                    try {
                        with(ktorClient()) {
                            sportLogin(username, password)
                            getSportScore<String>()
                        }
                    } catch (e: Exception) {
                        call.respond(CommonResult(HttpStatusCode.InternalServerError, "${e.javaClass} ${e.message}"))
                    }
                }
                get("/sport_morning") {}
                get("/sport_club") { }
            }
            route("/vpn2") {

            }
        }
    }
}