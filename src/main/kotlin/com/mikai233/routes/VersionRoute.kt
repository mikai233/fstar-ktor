@file:Suppress("DuplicatedCode")

package com.mikai233.routes

import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.mikai233.orm.*
import com.mikai233.service.*
import com.mikai233.tool.*
import com.qiniu.util.Auth
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/22
 */

@Suppress("UNCHECKED_CAST")
fun Application.versionRoute() {
    val gson by lazy { Gson() }

    routing {
        /**
         * 旧版客户端API接口V1
         */
        route("/api") {
            post("/version") {
                val encryptedBody = call.receive<Map<String, String>>()
                val data = encryptedBody["data"] ?: return@post
                val decryptString = AesCryptUtil.decryptS(data)
                val body = gson.fromJson(decryptString, Map::class.java) as? Map<String, String> ?: return@post
                val device = Device(
                    id = -1,
                    appVersion = body["version"].orEmpty(),
                    buildNumber = body["buildNumber"].orEmpty().toIntOrNull() ?: 0,
                    androidId = body["androidId"].orEmpty(),
                    brand = body["brand"].orEmpty(),
                    device = body["device"].orEmpty(),
                    platform = body["platform"].orEmpty(),
                    product = body["product"].orEmpty(),
                    model = body["model"].orEmpty(),
                    androidVersion = body["androidVersion"].orEmpty()
                )
                val exists = deviceService.getDeviceByAndroidId(device.androidId)
                if (exists == null) {
                    deviceService.addDevice(device)
                }
                redisService.zAddAndroidId(device.androidId)
            }
            //忽略此参数
            get("/changelog/{buildNumber}") {
                val versions = versionService.getAllVersion()
                val jsonString = gson.toJson(versions)
                AesCryptUtil.encryptS(jsonString).also {
                    call.respond(OldCommonResult(it))
                }
            }
            get("/service") {
                val key = call.request.queryParameters["key"]
                if (key == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                Redis.asyncIO { client ->
                    client[key]
                }.also {
                    val jsonString = gson.toJson(hashMapOf(key to it))
                    AesCryptUtil.encryptS(jsonString).also { encoded ->
                        call.respond(OldCommonResult(encoded))
                    }
                }
            }
            route("/score") {
                get("/class") {
                    val classNumber = call.request.queryParameters["classNumber"]
                    if (classNumber == null) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@get
                    }
                    scoreService.getScoresByClassNumber(classNumber).also { scores ->
                        val jsonString = gson.toJson(scores)
                        AesCryptUtil.encryptS(jsonString).also {
                            call.respond(OldCommonResult(it))
                        }
                    }
                }
                post {
                    val encryptedBody = call.receive<Map<String, String>>()
                    val data = encryptedBody["data"] ?: return@post
                    val decryptString = AesCryptUtil.decryptS(data)
                    val scores = gson.fromJson(decryptString, List::class.java) as? List<Score> ?: return@post
                    val exists = scores.firstOrNull()
                    if (exists != null) {
                        scoreService.deleteScoresByStudentNumber(exists.studentNumber)
                    }
                    scoreService.addScores(scores).also {
                        call.respond(it)
                    }
                }
            }
        }
        /**
         * 旧版客户端API接口V2
         */
        route("/v2") {
            route("/auth") {
                //登录
                post {
                    val loginRequest = call.receive<LoginRequest>()
                    if (loginRequest.username == null || loginRequest.password == null) {
                        call.respond(
                            OldCommonResult(
                                message = requestParamInvalid.description,
                                code = requestParamInvalid.value
                            )
                        )
                        return@post
                    }
                    val user = userService.getUsersByName(loginRequest.username).firstOrNull()
                    if (user == null) {
                        call.respond(OldCommonResult(message = userNotFound.description, code = userNotFound.value))
                        return@post
                    }
                    if (userService.matches(loginRequest.password, user.password)
                    ) {
                        call.respond(OldCommonResult(data = generateToken(user.username)))
                    } else {
                        call.respond(
                            OldCommonResult(
                                message = passwordIncorrect.description,
                                code = passwordIncorrect.value
                            )
                        )
                    }
                }
                //注册
                post("/register") {
                    val loginRequest = call.receive<LoginRequest>()
                    if (loginRequest.username == null || loginRequest.password == null) {
                        call.respond(
                            OldCommonResult(
                                message = requestParamInvalid.description,
                                code = requestParamInvalid.value
                            )
                        )
                        return@post
                    }
                    val user = userService.getUsersByName(loginRequest.username).firstOrNull()
                    if (user != null) {
                        call.respond(
                            OldCommonResult(
                                message = userAlreadyExists.description,
                                code = userAlreadyExists.value
                            )
                        )
                        return@post
                    }
                    userService.createUser(
                        User(
                            id = -1,
                            username = loginRequest.username,
                            password = userService.encode(loginRequest.password),
                            roles = listOf("ROLE_USER")
                        )
                    ).also {
                        call.respond(OldCommonResult(data = generateToken(loginRequest.username)))
                    }
                }
            }
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
                        call.respond(
                            OldCommonResult(
                                message = requestParamInvalid.description,
                                code = requestParamInvalid.value
                            )
                        )
                        return@get
                    }
                    val scores = scoreService.getScoresByClassNumber(classNumber)
                    call.respond(OldCommonResult(data = scores))
                }
                post {
                    //这里不知道为么传入List的类型在经过Gson反序列化之后都会变成LinkedTreeMap
                    val scoresMap = call.receive<List<LinkedTreeMap<String, String>>>()
                    val scores = scoresMap.map {
                        Score(
                            id = -1,
                            studentNumber = it["studentNumber"] ?: "",
                            no = it["no"] ?: "",
                            semester = it["semester"] ?: "",
                            scoreNo = it["scoreNo"] ?: "",
                            name = it["name"] ?: "",
                            score = it["score"] ?: "",
                            credit = it["credit"] ?: "",
                            period = it["period"] ?: "",
                            evaluationMode = it["evaluationMode"] ?: "",
                            courseProperty = it["courseProperty"] ?: "",
                            courseNature = it["courseNature"] ?: "",
                            alternativeCourseNumber = it["alternativeCourseNumber"] ?: "",
                            alternativeCourseName = it["alternativeCourseName"] ?: "",
                            scoreFlag = it["scoreFlag"] ?: ""
                        )
                    }
                    val exists = scores.firstOrNull()
                    if (exists != null) {
                        scoreService.deleteScoresByStudentNumber(exists.studentNumber)
                    }
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
                    call.respond(OldCommonResult())
                }
                get("/upload_token") {
                    val key = call.request.queryParameters["key"]
                    if (key == null) {
                        call.respond(
                            OldCommonResult(
                                message = requestParamInvalid.description,
                                code = requestParamInvalid.value
                            )
                        )
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
                /**
                 * 课表解析配置
                 */
                route("/config") {
                    get {
                        val queryParameters = call.request.queryParameters
                        val page = queryParameters["page"]?.toIntOrNull()
                        val size = queryParameters["size"]?.toIntOrNull()
                        if (page == null || size == null || page < 0 || size < 0) {
                            call.respond(
                                OldCommonResult(
                                    message = requestParamInvalid.description,
                                    code = requestParamInvalid.value
                                )
                            )
                            return@get
                        }
                        parseConfigService.getConfigsByPage(page, size).also {
                            call.respond(OldCommonResult(hashMapOf("content" to it)))
                        }
                    }
                    authenticate("ROLE_USER") {
                        get("/user") {
                            val principal = call.principal<JWTPrincipal>()
                            val username = principal!!.payload.getClaim("username").asString()
                            parseConfigService.getConfigsByUsername(username).also {
                                call.respond(OldCommonResult(it))
                            }
                        }
                        post {
                            val config = call.receive<ParseConfig>()
                            parseConfigService.addConfig(config).also {
                                call.respond(OldCommonResult(it))
                            }
                        }
                        delete {
                            val parameters = call.request.queryParameters
                            val id = parameters["id"]?.toIntOrNull()
                            if (id == null) {
                                call.respond(
                                    OldCommonResult(
                                        message = requestParamInvalid.description,
                                        code = requestParamInvalid.value
                                    )
                                )
                                return@delete
                            }
                            parseConfigService.deleteConfigById(id).also {
                                call.respond(OldCommonResult(id))
                            }
                        }
                    }
                    get("/school/{schoolName}") {
                        val schoolName = call.parameters["schoolName"]
                        if (schoolName == null) {
                            call.respond(
                                OldCommonResult(
                                    message = requestParamInvalid.description,
                                    code = requestParamInvalid.value
                                )
                            )
                            return@get
                        }
                        parseConfigService.getConfigsBySchoolName(schoolName).also {
                            call.respond(OldCommonResult(hashMapOf("content" to it)))
                        }
                    }
                }
                get("/code_host") {
                    call.respond(OldCommonResult(property("qiniu.codeHost")))
                }
            }
        }
        route("/v3") {
        }
    }
}