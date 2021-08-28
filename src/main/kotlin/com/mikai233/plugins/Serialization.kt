package com.mikai233.plugins

import com.google.gson.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*
import java.lang.reflect.Type
import java.time.LocalDateTime

/**
 * 老版本服务器返回给客户端的时间格式是**2021-07-11T23:32:58**这种形式
 * 为了做兼容处理，这里需要自定义时间的序列化方式
 */
object LocalDateTimeAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    override fun serialize(src: LocalDateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString())
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDateTime {
        TODO("Not yet implemented")
    }
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter)
        }
    }

    routing {
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}
