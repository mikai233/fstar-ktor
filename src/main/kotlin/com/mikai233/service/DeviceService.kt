@file:Suppress("DuplicatedCode", "unused")

package com.mikai233.service

import com.mikai233.orm.DB
import com.mikai233.orm.Device
import com.mikai233.orm.Devices
import com.mikai233.tool.asyncIO
import com.mikai233.tool.camelCase
import org.ktorm.dsl.*
import org.ktorm.entity.*

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/28
 */

class DeviceService {
    suspend fun getDeviceById(id: Int): Device? = DB.asyncIO {
        devices.find { it.id eq id }
    }

    suspend fun getDeviceByAndroidId(id: String) = DB.asyncIO {
        devices.findLast { it.androidId eq id }
    }

    suspend fun getDeviceByBuildNumberAndAndroidId(buildNumber: Int, androidId: String) = DB.asyncIO {
        devices.findLast { (it.buildNumber eq buildNumber) and (it.androidId eq androidId) }
    }

    suspend fun getDevicesByPage(page: Int, size: Int) = DB.asyncIO {
        require(page >= 0) { "page: $page must >= 0" }
        require(size >= 0) { "size: $size must >= 0" }
        devices.drop(page * size).take(size).toList()
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

    /**
     * 删除所有匹配AndroidId的设备记录
     */
    suspend fun deleteDevicesByAndroidId(id: String) = DB.asyncIO {
        database.delete(Devices) {
            it.androidId eq id
        }
    }

    suspend fun deleteDeviceById(id: Int) = DB.asyncIO {
        database.delete(Devices) {
            it.id eq id
        }
    }

    suspend fun groupDevices() = DB.asyncIO {
        val countMap = mutableMapOf<String, Map<String, Int>>()
        val appVersion = mutableMapOf<String, Int>()
        val buildNumber = mutableMapOf<String, Int>()
        val androidVersion = mutableMapOf<String, Int>()
        val brand = mutableMapOf<String, Int>()
        val device = mutableMapOf<String, Int>()
        val model = mutableMapOf<String, Int>()
        val product = mutableMapOf<String, Int>()
        val platform = mutableMapOf<String, Int>()
        devices.forEach {
            appVersion[it.appVersion] = appVersion.getOrDefault(it.appVersion, 1) + 1
            buildNumber[it.buildNumber.toString()] = buildNumber.getOrDefault(it.buildNumber.toString(), 1) + 1
            androidVersion[it.appVersion] = androidVersion.getOrDefault(it.androidVersion, 1) + 1
            brand[it.brand] = brand.getOrDefault(it.brand, 1) + 1
            device[it.device] = device.getOrDefault(it.device, 1) + 1
            model[it.model] = model.getOrDefault(it.model, 1) + 1
            product[it.device] = product.getOrDefault(it.product, 1) + 1
            platform[it.platform] = platform.getOrDefault(it.platform, 1) + 1
        }
        countMap[Devices.appVersion.name.camelCase()] = appVersion
        countMap[Devices.buildNumber.name.camelCase()] = buildNumber
        countMap[Devices.androidVersion.name.camelCase()] = androidVersion
        countMap[Devices.brand.name.camelCase()] = brand
        countMap[Devices.device.name.camelCase()] = device
        countMap[Devices.model.name.camelCase()] = model
        countMap[Devices.product.name.camelCase()] = product
        countMap[Devices.platform.name.camelCase()] = platform
        countMap
    }

    suspend fun countDevices() = DB.asyncIO {
        database.from(Devices).selectDistinct(Devices.androidId).totalRecords.run {
            mapOf("total" to this)
        }
    }
}