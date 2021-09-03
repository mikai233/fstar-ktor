package com.mikai233.service

import com.google.gson.Gson
import com.mikai233.orm.Message
import com.mikai233.orm.Redis
import com.mikai233.orm.Score
import com.mikai233.orm.Version
import com.mikai233.tool.Minutes
import com.mikai233.tool.asyncIO
import java.time.*
import java.time.temporal.TemporalAdjusters


/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/26
 */

/**
 * 用于统计用户活跃度等信息
 */
class RedisService {
    companion object {
        const val vitalityKey = "fstar_vitality"
        val gson = Gson()
    }

    suspend fun getSchoolBusUrl(): String = Redis.asyncIO { client ->
        client["school_bus"]
    }

    suspend fun getSchoolCalendarUlr(): String = Redis.asyncIO { client ->
        client["school_calendar"]
    }

    /**
     * Redis数据结构ZSET
     * key为[vitalityKey]
     * score为当前Unix时间戳
     * value为AndroidId
     */
    suspend fun zAddAndroidId(id: String): Long = Redis.asyncIO { client ->
        val localDateTime = LocalDateTime.now()
        client.zadd(vitalityKey, localDateTime.toEpochSecond(ZoneOffset.UTC).toDouble(), id)
    }

    /**
     * 当天活跃度
     */
    suspend fun currentDayVitality(): Set<String> = Redis.asyncIO { client ->
        val min = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toEpochSecond(ZoneOffset.UTC).toDouble()
        val max = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toEpochSecond(ZoneOffset.UTC).toDouble()
        client.zrangeByScore(vitalityKey, min, max)
    }

    /**
     * 当周活跃度
     */
    suspend fun currentWeekVitality(): Set<String> = Redis.asyncIO { client ->
        val min = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).with(DayOfWeek.MONDAY).toEpochSecond(ZoneOffset.UTC)
            .toDouble()
        val max = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).with(DayOfWeek.SUNDAY).toEpochSecond(ZoneOffset.UTC)
            .toDouble()
        client.zrangeByScore(vitalityKey, min, max)
    }

    /**
     * 当月活跃度
     */
    suspend fun currentMonthVitality(): Set<String> = Redis.asyncIO { client ->
        val min = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).with(TemporalAdjusters.firstDayOfMonth())
            .toEpochSecond(ZoneOffset.UTC).toDouble()
        val max = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).with(TemporalAdjusters.lastDayOfMonth())
            .toEpochSecond(ZoneOffset.UTC).toDouble()
        client.zrangeByScore(vitalityKey, min, max)
    }

    /**
     * 某一天的活跃度
     */
    suspend fun dayVitality(date: LocalDate): Set<String> = Redis.asyncIO { client ->
        val min = LocalDateTime.of(date, LocalTime.MIN).toEpochSecond(ZoneOffset.UTC).toDouble()
        val max = LocalDateTime.of(date, LocalTime.MAX).toEpochSecond(ZoneOffset.UTC).toDouble()
        client.zrangeByScore(vitalityKey, min, max)
    }

    /**
     * 某一周的活跃度
     */
    suspend fun weekVitality(date: LocalDate): Set<String> = Redis.asyncIO { client ->
        val min =
            LocalDateTime.of(date, LocalTime.MIN).with(DayOfWeek.MONDAY).toEpochSecond(ZoneOffset.UTC)
                .toDouble()
        val max =
            LocalDateTime.of(date, LocalTime.MAX).with(DayOfWeek.SUNDAY).toEpochSecond(ZoneOffset.UTC)
                .toDouble()
        client.zrangeByScore(vitalityKey, min, max)
    }

    /**
     * 某一月的活跃度
     */
    suspend fun monthVitality(date: LocalDate): Set<String> = Redis.asyncIO { client ->
        val min = LocalDateTime.of(date, LocalTime.MIN).with(TemporalAdjusters.firstDayOfMonth())
            .toEpochSecond(ZoneOffset.UTC).toDouble()
        val max = LocalDateTime.of(date, LocalTime.MAX).with(TemporalAdjusters.lastDayOfMonth())
            .toEpochSecond(ZoneOffset.UTC).toDouble()
        client.zrangeByScore(vitalityKey, min, max)
    }

    suspend fun getLatestMessageCache(): Message? = Redis.asyncIO { client ->
        client.get("latest_message").run {
            gson.fromJson(this, Message::class.java)
        }
    }

    suspend fun setLatestMessageCache(message: Message): String = Redis.asyncIO { client ->
        val key = "latest_message"
        client.set(key, gson.toJson(message)).also {
            client.expire(key, 10 * Minutes)
        }
    }

    suspend fun getCurrentVersionCache(): Version? = Redis.asyncIO { client ->
        client.get("current_version").run {
            gson.fromJson(this, Version::class.java)
        }
    }

    suspend fun setCurrentVersionCache(version: Version): String = Redis.asyncIO { client ->
        val key = "current_version"
        client.set(key, gson.toJson(version)).also {
            client.expire(key, 10 * Minutes)
        }
    }

    suspend fun getAllMessagesCache(): List<Message> = Redis.asyncIO { client ->
        client.smembers("all_message").map {
            gson.fromJson(it, Message::class.java)
        }
    }

    suspend fun setAllMessagesCache(messages: List<Message>) = Redis.asyncIO { client ->
        val key = "all_message"
        invalidCache(key)
        messages.forEach {
            client.sadd(key, gson.toJson(it))
        }
    }

    suspend fun getScoresCacheByClassNumber(number: String) = Redis.asyncIO { client ->
        client.smembers("scores_class_number_$number").map { gson.fromJson(it, Score::class.java) }
    }

    suspend fun setScoresCacheByClassNumber(number: String, scores: List<Score>) = Redis.asyncIO { client ->
        val key = "scores_class_number_$number"
        invalidCache(key)
        scores.forEach {
            client.sadd(key, gson.toJson(it))
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    suspend fun invalidCache(key: String): Long = Redis.asyncIO { client ->
        client.del(key)
    }
}