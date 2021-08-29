package com.mikai233.orm

import org.ktorm.dsl.QueryRowSet
import org.ktorm.schema.BaseTable
import org.ktorm.schema.int
import org.ktorm.schema.varchar

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/22
 */

data class Device(
    val id: Int,
    val appVersion: String,
    val buildNumber: Int,
    val androidId: String,
    val androidVersion: String,
    val brand: String,
    val device: String,
    val model: String,
    val product: String,
    val platform: String
)

object Devices : BaseTable<Device>("device") {
    val id = int("id").primaryKey()
    val appVersion = varchar("app_version")
    val buildNumber = int("build_number")
    val androidId = varchar("android_id")
    val androidVersion = varchar("android_version")
    val brand = varchar("brand")
    val device = varchar("device")
    val model = varchar("model")
    val product = varchar("product")
    val platform = varchar("platform")
    override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean) = Device(
        id = row[id] ?: 0,
        appVersion = row[appVersion].orEmpty(),
        buildNumber = row[buildNumber] ?: 0,
        androidId = row[androidId] ?: "",
        androidVersion = row[androidVersion].orEmpty(),
        brand = row[brand].orEmpty(),
        device = row[device].orEmpty(),
        model = row[model].orEmpty(),
        product = row[product].orEmpty(),
        platform = row[platform].orEmpty()
    )
}