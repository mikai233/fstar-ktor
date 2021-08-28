package com.mikai233.service

import com.mikai233.orm.Redis
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
        const val vitalityKey = "fstar-vitality"
    }

    suspend fun getSchoolBusUrl(): String = Redis.asyncIO {
        client["school_bus"]
    }

    suspend fun getSchoolCalendarUlr(): String = Redis.asyncIO {
        client["school_calendar"]
    }

    /**
     * Redis数据结构ZSET
     * key为[vitalityKey]
     * score为当前Unix时间戳
     * value为AndroidId
     */
    suspend fun zAddAndroidId(id: String): Long = Redis.asyncIO {
        val localDateTime = LocalDateTime.now()
        client.zadd(vitalityKey, localDateTime.toEpochSecond(ZoneOffset.UTC).toDouble(), id)
    }

    /**
     * 当天活跃度
     */
    suspend fun currentDayVitality(): Set<String> = Redis.asyncIO {
        val min = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toEpochSecond(ZoneOffset.UTC).toDouble()
        val max = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toEpochSecond(ZoneOffset.UTC).toDouble()
        client.zrangeByScore(vitalityKey, min, max)
    }

    /**
     * 当周活跃度
     */
    suspend fun currentWeekVitality(): Set<String> = Redis.asyncIO {
        val min = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).with(DayOfWeek.MONDAY).toEpochSecond(ZoneOffset.UTC)
            .toDouble()
        val max = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).with(DayOfWeek.SUNDAY).toEpochSecond(ZoneOffset.UTC)
            .toDouble()
        client.zrangeByScore(vitalityKey, min, max)
    }

    /**
     * 当月活跃度
     */
    suspend fun currentMonthVitality(): Set<String> = Redis.asyncIO {
        val min = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).with(TemporalAdjusters.firstDayOfMonth())
            .toEpochSecond(ZoneOffset.UTC).toDouble()
        val max = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).with(TemporalAdjusters.lastDayOfMonth())
            .toEpochSecond(ZoneOffset.UTC).toDouble()
        client.zrangeByScore(vitalityKey, min, max)
    }

    /**
     * 某一天的活跃度
     */
    suspend fun dayVitality(date: LocalDate): Set<String> = Redis.asyncIO {
        val min = LocalDateTime.of(date, LocalTime.MIN).toEpochSecond(ZoneOffset.UTC).toDouble()
        val max = LocalDateTime.of(date, LocalTime.MAX).toEpochSecond(ZoneOffset.UTC).toDouble()
        client.zrangeByScore(vitalityKey, min, max)
    }

    /**
     * 某一周的活跃度
     */
    suspend fun weekVitality(date: LocalDate): Set<String> = Redis.asyncIO {
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
    suspend fun monthVitality(date: LocalDate): Set<String> = Redis.asyncIO {
        val min = LocalDateTime.of(date, LocalTime.MIN).with(TemporalAdjusters.firstDayOfMonth())
            .toEpochSecond(ZoneOffset.UTC).toDouble()
        val max = LocalDateTime.of(date, LocalTime.MAX).with(TemporalAdjusters.lastDayOfMonth())
            .toEpochSecond(ZoneOffset.UTC).toDouble()
        client.zrangeByScore(vitalityKey, min, max)
    }
}