package com.mikai233.routes

import com.mikai233.orm.*
import com.mikai233.service.*
import com.mikai233.tool.property
import com.mikai233.tool.requestParamInvalid
import com.qiniu.util.Auth
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
                post {
                    val scores = call.receive<List<Score>>()
                    val result = scoreService.addScores(scores)
                    call.respond(OldCommonResult(data = result))
                }
            }
            /**
             * 消息
             */
            route("/message") {
                //全部消息
                get {
                    val messages = messageService.getMessages()
                    call.respond(OldCommonResult(messages))
                }
                //最新一条消息
                get("/latest") {
                    val current = messageService.getCurrentMessage()
                    call.respond(OldCommonResult(current))
                }
            }
            /**
             * 服务
             */
            route("/service") {
                /**
                 * 这里根据AndroidId进行设备的区分
                 * 由于没有获取Android电话权限，而采用AndroidId来标识设备，AndroidId有存在改变的可能
                 * 这里仅对传入的设备进行存库操作，实际的用户数可能会小于统计的用户数，因为AndroidId有改变的可能
                 * 用户的活跃度采用Redis进行统计
                 */
                post("/vitality") {
                    val device = call.receive<Device>()
                    redisService.zAddAndroidId(device.androidId)
                    val exists = deviceService.getDeviceByAndroidId(device.androidId)
                    if (exists == null) {
                        deviceService.addDevice(device)
                    }
                }
                get("/upload_token") {
                    val key = call.request.queryParameters["key"]
                    if (key == null) {
                        call.respond(requestParamInvalid)
                        return@get
                    }
                    val auth = Auth.create(property("qiniu.accessKey"), property("qiniu.secretKey"))
                    val token = auth.uploadToken(property("qiniu.bucket"), key, 60, null)
                    call.respond(OldCommonResult(token))
                }
                route("/just") {
                    //校车
                    get("/school_bus") {
                        val url = redisService.getSchoolBusUrl()
                        call.respond(OldCommonResult(url))
                    }
                    //校历
                    get("/school_calendar") {
                        val url = redisService.getSchoolCalendarUlr()
                        call.respond(OldCommonResult(url))
                    }
                }
            }
        }
        route("/v3") {

        }
    }
}