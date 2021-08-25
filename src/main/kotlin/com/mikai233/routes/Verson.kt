package com.mikai233.routes

import com.mikai233.orm.CommonResult
import com.mikai233.orm.Version
import com.mikai233.service.versionService
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/22
 */

fun Application.versionRoute() {
    routing {
        route("/version") {
            get("/current") {
                val current = versionService.getCurrentVersion()
                call.respond(CommonResult(data = current))
            }
            get("/all") {
                val versions = versionService.getAllVersion()
                call.respond(CommonResult(data = versions))
            }
            post("/add") {
                val version = call.receive<Version>()
                versionService.addVersion(version).also {
                    call.respond(CommonResult(data = it))
                }
            }
            delete("/{id}") {
                call.parameters["id"]?.toIntOrNull()?.let { id ->
                    versionService.deleteVersionById(id).also {
                        call.respond(CommonResult(data = it))
                    }
                }
            }
        }
    }
}