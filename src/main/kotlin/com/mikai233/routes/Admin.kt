package com.mikai233.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.mikai233.orm.CommonResult
import com.mikai233.orm.DB
import com.mikai233.orm.LoginRequest
import com.mikai233.service.userService
import com.mikai233.tool.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.entity.drop
import org.ktorm.entity.map
import org.ktorm.entity.take
import java.util.*

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
                val users = userService.getUserByName(loginRequest.username)
                if (users.isNotEmpty()) {
                    val user = users.first()
                    val match = userService.passwordEncoder.matches(loginRequest.password, user.password)
                    if (match) {
                        val audience = property("jwt.audience")
                        val issuer = property("jwt.issuer")
                        val secret = property("jwt.secret")
                        val token = JWT.create()
                            .withAudience(audience)
                            .withIssuer(issuer)
                            .withClaim("username", user.username)
                            .withExpiresAt(Date(System.currentTimeMillis() + 10 * Minutes))
                            .sign(Algorithm.HMAC256(secret))
                        call.respond(
                            CommonResult(
                                data = hashMapOf(
                                    "token" to token,
                                    "tokenHeader" to property("jwt.tokenHeader"),
                                    "tokenPrefix" to property("jwt.tokenPrefix")
                                )
                            )
                        )
                    } else {
                        call.respond(CommonResult(passwordIncorrect))
                    }
                } else {
                    call.respond(CommonResult(httpStatusCode = userNotFound))
                }
            }
            /**
             * 鉴权接口
             */
            authenticate("ROLE_ADMIN") {
                /**
                 * 分页获取用户信息
                 */
                get("/user") {
                    val parameters = call.request.queryParameters
                    val page = parameters["page"]?.toInt()
                    val size = parameters["size"]?.toInt()
                    if (page == null || size == null || page < 1 || size < 0) {
                        call.respond(CommonResult(requestParamInvalid))
                        return@get
                    }
                    val users = DB.asyncIO {
                        //用户的密码不返回给客户端
                        users.drop((page - 1) * size).take(size).map { it.copy(password = "") }.toList()
                    }
                    call.respond(HttpStatusCode.OK, CommonResult(data = users))
                }
                /**
                 * 按username获取用户信息
                 */
                get("/user/username/{username}") {

                }
                /**
                 * 按id获取用户信息
                 */
                get("/user/id/{id}") {

                }
            }
        }
    }
}