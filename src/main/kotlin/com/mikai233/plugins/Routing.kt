package com.mikai233.plugins

import com.mikai233.routes.adminRoute
import com.mikai233.routes.versionRoute
import io.ktor.application.*

fun Application.configureRouting() {
    versionRoute()
    adminRoute()
}
