package com.mikai233.orm

import org.ktorm.dsl.QueryRowSet
import org.ktorm.schema.BaseTable
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import java.time.LocalDateTime

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/28
 */

data class Message(
    val id: Int,
    val content: String,
    val publishTime: LocalDateTime,
    val maxVisibleBuildNumber: Int,
    val minVisibleBuildNumber: Int,
)

object Messages : BaseTable<Message>("message") {
    val id = int("id").primaryKey()
    val content = varchar("content")
    val publishTime = datetime("publish_time")
    val maxVisibleBuildNumber = int("max_visible_build_number")
    val minVisibleBuildNumber = int("min_visible_build_number")
    override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean) = Message(
        id = row[id] ?: 0,
        content = row[content].orEmpty(),
        publishTime = row[publishTime] ?: LocalDateTime.MIN,
        maxVisibleBuildNumber = row[maxVisibleBuildNumber] ?: 0,
        minVisibleBuildNumber = row[minVisibleBuildNumber] ?: 0,
    )
}