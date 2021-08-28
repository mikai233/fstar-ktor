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

data class ParseConfig(
    val id: Int,
    val schoolName: String,
    val schoolUrl: String,
    val user: String,
    val author: String,
    val preUrl: String,
    val codeUrl: String,
    val publishTime: LocalDateTime,
    val remark: String,
    val download: Int,
)

object ParseConfigs : BaseTable<ParseConfig>("parse_config") {
    val id = int("id")
    val schoolName = varchar("school_name")
    val schoolUrl = varchar("school_url")
    val user = varchar("user")
    val author = varchar("author")
    val preUrl = varchar("pre_url")
    val codeUrl = varchar("code_url")
    val publishTime = datetime("publish_time")
    val remark = varchar("remark")
    val download = int("download")
    override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean) = ParseConfig(
        id = row[id] ?: 0,
        schoolName = row[schoolName].orEmpty(),
        schoolUrl = row[schoolUrl].orEmpty(),
        user = row[user].orEmpty(),
        author = row[author].orEmpty(),
        preUrl = row[preUrl].orEmpty(),
        codeUrl = row[codeUrl].orEmpty(),
        publishTime = row[publishTime] ?: LocalDateTime.MIN,
        remark = row[remark].orEmpty(),
        download = row[download] ?: 0,
    )
}
