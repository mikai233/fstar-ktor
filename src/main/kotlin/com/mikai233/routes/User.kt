package com.mikai233.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.mikai233.orm.CommonResult
import com.mikai233.orm.DB
import com.mikai233.service.userService
import com.mikai233.tool.asyncIO
import com.mikai233.tool.property
import com.mikai233.tool.userNotFound
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.entity.map
import java.util.*

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/22
 */

fun Application.adminRoute() {

    routing {
        route("/admin") {
            post("/auth") {
                val audience = property("jwt.audience")
                val issuer = property("jwt.issuer")
                val secret = property("jwt.secret")
                val users = userService.getUserByName("dreamfever")
                if (users.isNotEmpty()) {
                    val user = users.first()
                    val token = JWT.create()
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .withClaim("username", user.username)
                        .withExpiresAt(Date(System.currentTimeMillis() + 60000))
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
                    call.respond(CommonResult(httpStatusCode = userNotFound))
                }
            }
            get("/user") {
                val users = DB.asyncIO {
                    //用户的密码不返回给客户端
                    users.map { it.copy(password = "") }.toList()
                }
                call.respond(HttpStatusCode.OK, CommonResult(data = users))
            }
        }
    }
}