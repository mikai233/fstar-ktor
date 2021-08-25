package com.mikai233.routes

import com.mikai233.orm.DB
import com.mikai233.orm.Devices
import com.mikai233.tool.asyncIO
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.dsl.from
import org.ktorm.dsl.map
import org.ktorm.dsl.select

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/22
 */

fun Application.versionRoute() {
    routing {
        route("/version") {
            get("/current") {
                val e = DB.asyncIO {
                    database.from(Devices).select().map { Devices.createEntity(it) }
                }
                call.respond(e)
            }
            get("/all") {
                call.respondText { "all version" }
            }
        }
    }
}