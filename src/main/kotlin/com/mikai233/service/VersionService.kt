package com.mikai233.service

import com.mikai233.orm.DB
import com.mikai233.orm.Version
import com.mikai233.orm.Versions
import com.mikai233.tool.asyncIO
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.removeIf
import org.ktorm.entity.sortedByDescending
import org.ktorm.entity.toList

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/25
 */

class VersionService {
    suspend fun addVersion(version: Version): Int {
        return DB.asyncIO {
            database.insert(Versions) {
                with(version) {
                    set(Versions.buildNumber, buildNumber)
                    set(Versions.version, this.version)
                    set(Versions.description, description)
                    set(Versions.downloadUrl, downloadUrl)
                }
            }
        }
    }

    suspend fun deleteVersionById(id: Int): Int {
        return DB.asyncIO {
            versions.removeIf { it.id eq id }
        }
    }

    suspend fun getAllVersion(): List<Version> {
        return DB.asyncIO {
            versions.toList()
        }
    }

    suspend fun getCurrentVersion(): Version? {
        return DB.asyncIO {
            versions.sortedByDescending { it.buildNumber }.firstOrNull()
        }
    }
}