@file:Suppress("unused", "DuplicatedCode")

package com.mikai233.service

import com.mikai233.orm.DB
import com.mikai233.orm.ParseConfig
import com.mikai233.orm.ParseConfigs
import com.mikai233.tool.asyncIO
import org.ktorm.dsl.*
import org.ktorm.entity.filter
import org.ktorm.entity.findLast
import java.time.LocalDateTime

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/28
 */

class ParseConfigService {
    suspend fun getConfigById(id: Int) = DB.asyncIO {
        parseConfigs.findLast { it.id eq id }
    }

    suspend fun getConfigsByAuthor(name: String) = DB.asyncIO {
        parseConfigs.filter { it.author eq name }
    }

    suspend fun getConfigsBySchoolName(name: String) = DB.asyncIO {
        parseConfigs.filter { it.schoolName eq name }
    }

    suspend fun deleteConfigById(id: Int) = DB.asyncIO {
        database.delete(ParseConfigs) {
            it.id eq id
        }
    }

    suspend fun deleteAllConfigs() = DB.asyncIO {
        database.deleteAll(ParseConfigs)
    }

    suspend fun addConfig(config: ParseConfig) = DB.asyncIO {
        database.insert(ParseConfigs) {
            with(config) {
                set(ParseConfigs.schoolName, schoolName)
                set(ParseConfigs.schoolUrl, schoolUrl)
                set(ParseConfigs.user, user)
                set(ParseConfigs.author, author)
                set(ParseConfigs.preUrl, preUrl)
                set(ParseConfigs.codeUrl, codeUrl)
                set(ParseConfigs.publishTime, LocalDateTime.now())
                set(ParseConfigs.remark, remark)
                set(ParseConfigs.download, download)
            }
        }
    }

    suspend fun updateConfig(config: ParseConfig) = DB.asyncIO {
        database.update(ParseConfigs) {
            with(config) {
                where { it.id eq id }
                set(ParseConfigs.schoolName, schoolName)
                set(ParseConfigs.schoolUrl, schoolUrl)
                set(ParseConfigs.user, user)
                set(ParseConfigs.author, author)
                set(ParseConfigs.preUrl, preUrl)
                set(ParseConfigs.codeUrl, codeUrl)
                set(ParseConfigs.publishTime, LocalDateTime.now())
                set(ParseConfigs.remark, remark)
                set(ParseConfigs.download, download)
            }
        }
    }
}