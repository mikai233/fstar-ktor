@file:Suppress("DuplicatedCode", "unused")

package com.mikai233.service

import com.mikai233.orm.DB
import com.mikai233.orm.Device
import com.mikai233.orm.Devices
import com.mikai233.tool.asyncIO
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.dsl.update
import org.ktorm.entity.drop
import org.ktorm.entity.findLast
import org.ktorm.entity.take
import org.ktorm.entity.toList

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/28
 */

class DeviceService {
    suspend fun getDeviceById(id: Int): Device? = DB.asyncIO {
        devices.findLast { it.id eq id }
    }

    suspend fun getDeviceByAndroidId(id: String) = DB.asyncIO {
        devices.findLast { it.androidId eq id }
    }

    suspend fun getDevicesByPage(page: Int, size: Int): List<Device> = DB.asyncIO {
        require(page > 0) { "page: $page must > 0" }
        require(size >= 0) { "size: $size must >= 0" }
        devices.drop((page - 1) * size).take(size).toList()
    }

    suspend fun addDevice(device: Device) = DB.asyncIO {
        database.insert(Devices) {
            with(device) {
                set(Devices.appVersion, appVersion)
                set(Devices.buildNumber, buildNumber)
                set(Devices.androidId, androidId)
                set(Devices.androidVersion, androidVersion)
                set(Devices.brand, brand)
                set(Devices.device, this.device)
                set(Devices.model, model)
                set(Devices.product, product)
                set(Devices.platform, platform)
            }
        }
    }

    /**
     * 正常情况下AndroidId不会改变
     * 但是系统更新可能会改变AndroidId
     */
    suspend fun updateDevice(device: Device) = DB.asyncIO {
        database.update(Devices) {
            where { it.androidId eq device.androidId }
            with(device) {
                set(Devices.appVersion, appVersion)
                set(Devices.buildNumber, buildNumber)
                set(Devices.androidVersion, androidVersion)
                set(Devices.brand, brand)
                set(Devices.device, this.device)
                set(Devices.model, model)
                set(Devices.product, product)
                set(Devices.platform, platform)
            }
        }
    }
}