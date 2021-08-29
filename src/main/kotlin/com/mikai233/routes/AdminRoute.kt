@file:Suppress("DuplicatedCode")

package com.mikai233.routes

import com.mikai233.orm.CommonResult
import com.mikai233.orm.LoginRequest
import com.mikai233.service.redisService
import com.mikai233.service.userService
import com.mikai233.tool.generateToken
import com.mikai233.tool.passwordIncorrect
import com.mikai233.tool.requestParamInvalid
import com.mikai233.tool.userNotFound
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/22
 */

/**
 * 管理员接口
 * 使用JWT进行鉴权
 * 提供软件更新、消息发布、数据统计等接口
 */
fun Application.adminRoute() {

    routing {
        route("/admin") {
            /**
             * 登录接口
             */
            post("/auth") {
                val loginRequest = call.receive<LoginRequest>()
                if (loginRequest.username == null || loginRequest.password == null) {
                    call.respond(CommonResult(requestParamInvalid))
                    return@post
                }
                val user = userService.getUsersByName(loginRequest.username).firstOrNull()
                if (user != null) {
                    if (userService.matches(loginRequest.password, user.password)) {
                        call.respond(CommonResult(data = generateToken(user.username)))
                    } else {
                        call.respond(CommonResult(passwordIncorrect))
                    }
                } else {
                    call.respond(CommonResult(httpStatusCode = userNotFound))
                }
            }
            /**
             * 需要鉴权的接口
             */
            authenticate("ROLE_ADMIN") {
                route("/user") {
                    /**
                     * 分页获取用户信息
                     */
                    get {
                        val parameters = call.request.queryParameters
                        val page = parameters["page"]?.toInt()
                        val size = parameters["size"]?.toInt()
                        if (page == null || size == null || page < 0 || size < 0) {
                            call.respond(CommonResult(requestParamInvalid))
                            return@get
                        }
                        //密码不返回给客户端
                        val users = userService.getUsersByPage(page, size).map { it.copy(password = "") }
                        call.respond(CommonResult(data = users))
                    }
                    /**
                     * 按username获取用户信息
                     */
                    get("/username/{username}") {
                        val username = call.parameters["username"]
                        if (username == null) {
                            call.respond(CommonResult(requestParamInvalid))
                            return@get
                        }
                        val users = userService.getUsersByName(username).map { it.copy(password = "") }
                        call.respond(CommonResult(data = users))
                    }
                    /**
                     * 按id获取用户信息
                     */
                    get("/id/{id}") {
                        val id = call.parameters["id"]?.toIntOrNull()
                        if (id == null) {
                            call.respond(CommonResult(requestParamInvalid))
                            return@get
                        }
                        val user = userService.getUserById(id)
                        call.respond(CommonResult(data = user))
                    }
                }
                route("/vitality") {
                    get("/day") {
                        redisService.currentDayVitality().size.also {
                            call.respond(CommonResult(data = it))
                        }
                    }
                    get("/week") {
                        redisService.currentWeekVitality().size.also {
                            call.respond(CommonResult(data = it))
                        }
                    }
                    get("/month") {
                        redisService.currentMonthVitality().also {
                            call.respond(CommonResult(data = it))
                        }
                    }
                    route("/details") {
                        get("/day") {
                            redisService.currentDayVitality().also {
                                call.respond(CommonResult(data = it))
                            }
                        }
                        get("/week") {
                            redisService.currentWeekVitality().also {
                                call.respond(CommonResult(data = it))
                            }
                        }
                        get("/month") {
                            redisService.currentMonthVitality().also {
                                call.respond(CommonResult(data = it))
                            }
                        }
                    }
                }
            }
        }
    }
}