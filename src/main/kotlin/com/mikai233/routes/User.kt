package com.mikai233.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.mikai233.orm.DB
import com.mikai233.service.ResultWrap
import com.mikai233.service.userService
import com.mikai233.tool.asyncIO
import com.mikai233.tool.property
import com.mikai233.tool.wrapResult
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.entity.toList
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
                    call.respond(wrapResult(data = hashMapOf("token" to token)))
                } else {
                    call.respond("user not found")
                }
            }
            get("/user") {
                val users = DB.asyncIO {
                    users.toList()
                }
                call.respond(ResultWrap(data = users))
            }
        }
    }
}