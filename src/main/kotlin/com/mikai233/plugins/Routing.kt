package com.mikai233.plugins

import com.mikai233.routes.adminRoute
import com.mikai233.routes.versionRoute
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {
    versionRoute()
    adminRoute()
    val r = routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }

    r.route("a") {
    }
}
