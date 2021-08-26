package com.mikai233.plugins

import com.mikai233.routes.adminRoute
import com.mikai233.routes.versionRoute
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {
    versionRoute()
    adminRoute()
}
