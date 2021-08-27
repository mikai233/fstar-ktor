package com.mikai233.routes

import com.mikai233.orm.CommonResult
import com.mikai233.orm.OldCommonResult
import com.mikai233.orm.Version
import com.mikai233.service.scoreService
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
        /**
         * 旧版客户端API接口
         */
        route("/v2") {
            /**
             * 更新日志
             */
            route("/changelog") {
                get {
                    val versions = versionService.getAllVersion().sortedByDescending { it.buildNumber }
                    call.respond(OldCommonResult(data = versions))
                }
                get("/current_version") {
                    val currentVersion = versionService.getCurrentVersion()
                    println(currentVersion)
                    call.respond(OldCommonResult(data = currentVersion))
                }
            }
            /**
             * 成绩
             */
            route("/score") {
                get("/class/{classNumber}") {
                    val classNumber = call.parameters["classNumber"]
                    if (classNumber == null) {
                        call.respond(OldCommonResult(message = "参数不正确", code = 4001))
                        return@get
                    }
                    val scores = scoreService.getScoresByClassNumber(classNumber)
                    call.respond(OldCommonResult(data = scores))
                }
            }
        }
        route("/v3") {

        }
    }
}