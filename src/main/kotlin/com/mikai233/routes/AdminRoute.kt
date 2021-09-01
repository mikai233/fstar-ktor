@file:Suppress("DuplicatedCode")

package com.mikai233.routes

import com.mikai233.orm.CommonResult
import com.mikai233.orm.Device
import com.mikai233.orm.LoginRequest
import com.mikai233.service.deviceService
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
import java.time.Instant
import java.time.LocalDate

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
                //用户统计接口
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
                //活跃度统计接口
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
                        redisService.currentMonthVitality().size.also {
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
                    /**
                     * 传入具体的Unix时间戳，返回天、周、月范围内的活跃度
                     */
                    route("/concretely") {
                        //具体到某一天的活跃度，毫秒级Unix时间戳
                        get("/day") {
                            val timestamp = call.request.queryParameters["timestamp"]?.toLongOrNull()
                            if (timestamp == null) {
                                call.respond(requestParamInvalid)
                                return@get
                            }
                            redisService.dayVitality(LocalDate.from(Instant.ofEpochMilli(timestamp)))
                        }
                        //具体到某一周的活跃度，毫秒级Unix时间戳
                        get("/week") {
                            val timestamp = call.request.queryParameters["timestamp"]?.toLongOrNull()
                            if (timestamp == null) {
                                call.respond(requestParamInvalid)
                                return@get
                            }
                            redisService.weekVitality(LocalDate.from(Instant.ofEpochMilli(timestamp)))
                        }
                        //具体到某一月的活跃度，毫秒级Unix时间戳
                        get("/month") {
                            val timestamp = call.request.queryParameters["timestamp"]?.toLongOrNull()
                            if (timestamp == null) {
                                call.respond(requestParamInvalid)
                                return@get
                            }
                            redisService.monthVitality(LocalDate.from(Instant.ofEpochMilli(timestamp)))
                        }
                    }
                }
                //设备统计接口
                route("/device") {
                    get {
                        val queryParameters = call.request.queryParameters
                        val page = queryParameters["page"]?.toIntOrNull()
                        val size = queryParameters["size"]?.toIntOrNull()
                        if (page == null || size == null || page < 0 || size < 0) {
                            call.respond(requestParamInvalid)
                            return@get
                        }
                        deviceService.getDevicesByPage(page, size).also {
                            call.respond(CommonResult(data = it))
                        }
                    }
                    get("/id/{id}") {
                        val id = call.parameters["id"]?.toIntOrNull()
                        if (id == null) {
                            call.respond(requestParamInvalid)
                            return@get
                        }
                        deviceService.getDeviceById(id).also {
                            call.respond(CommonResult(data = it))
                        }
                    }
                    get("/android_id/{androidId}") {
                        val androidId = call.parameters["androidId"]
                        if (androidId == null) {
                            call.respond(requestParamInvalid)
                            return@get
                        }
                        deviceService.getDeviceByAndroidId(androidId).also {
                            call.respond(CommonResult(data = it))
                        }
                    }
                    post {
                        val device = call.receive<Device>()
                        deviceService.addDevice(device).also {
                            call.respond(CommonResult(data = it))
                        }
                    }
                    patch {
                        val device = call.receive<Device>()
                        deviceService.updateDevice(device).also {
                            call.respond(CommonResult(data = it))
                        }
                    }
                }
            }
        }
    }
}