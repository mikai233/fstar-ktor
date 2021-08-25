package com.mikai233.orm

import org.ktorm.dsl.QueryRowSet
import org.ktorm.schema.BaseTable
import org.ktorm.schema.varchar

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/22
 */

data class Device(
    val androidId: String,
    val brand: String,
    val device: String,
    val androidVersion: String,
    val model: String,
    val product: String,
)

object Devices : BaseTable<Device>("device") {
    private val androidId = varchar("android_id").primaryKey()
    private val brand = varchar("brand")
    private val device = varchar("device")
    private val androidVersion = varchar("android_version")
    private val model = varchar("model")
    private val product = varchar("product")
    override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean) = Device(
        androidId = row[androidId] ?: "",
        brand = row[brand].orEmpty(),
        device = row[device].orEmpty(),
        androidVersion = row[androidVersion].orEmpty(),
        model = row[model].orEmpty(),
        product = row[product].orEmpty()
    )
}