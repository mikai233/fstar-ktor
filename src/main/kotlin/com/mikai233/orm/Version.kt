package com.mikai233.orm

import org.ktorm.dsl.QueryRowSet
import org.ktorm.schema.BaseTable
import org.ktorm.schema.int
import org.ktorm.schema.varchar

/**
 * 更新日志
 */
data class Version(
    val id: Int,
    val buildNumber: Int,
    val version: String,
    val description: String,
    val downloadUrl: String,
)

object Versions : BaseTable<Version>("changelog_v2") {
    val id = int("id").primaryKey()
    val buildNumber = int("build_number")
    val version = varchar("version")
    val description = varchar("description")
    val downloadUrl = varchar("download_url")
    override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean) = Version(
        id = row[id] ?: 0,
        buildNumber = row[buildNumber] ?: 0,
        version = row[version].orEmpty(),
        description = row[description].orEmpty(),
        downloadUrl = row[downloadUrl].orEmpty(),
    )
}