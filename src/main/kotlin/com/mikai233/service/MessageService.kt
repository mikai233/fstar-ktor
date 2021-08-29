@file:Suppress("unused", "DuplicatedCode")

package com.mikai233.service

import com.mikai233.orm.DB
import com.mikai233.orm.Message
import com.mikai233.orm.Messages
import com.mikai233.tool.asyncIO
import org.ktorm.dsl.*
import org.ktorm.entity.*

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/28
 */

class MessageService {
    suspend fun getMessageById(id: Int) = DB.asyncIO {
        messages.find { it.id eq id }
    }

    suspend fun getCurrentMessage() = DB.asyncIO {
        messages.sortedByDescending { it.publishTime }.firstOrNull()
    }

    suspend fun getMessagesByPage(page: Int, size: Int) = DB.asyncIO {
        require(page >= 0) { "page: $page must >= 0" }
        require(size >= 0) { "size: $size must >= 0" }
        messages.sortedByDescending { it.publishTime }.drop(page * size).take(size).toList()
    }

    suspend fun getMessages() = DB.asyncIO {
        messages.sortedByDescending { it.publishTime }.toList()
    }

    suspend fun updateMessage(message: Message) = DB.asyncIO {
        database.update(Messages) {
            with(message) {
                where { it.id eq id }
                set(Messages.content, content)
                set(Messages.publishTime, publishTime)
                set(Messages.maxVisibleBuildNumber, maxVisibleBuildNumber)
                set(Messages.minVisibleBuildNumber, minVisibleBuildNumber)
            }
        }
    }

    suspend fun addMessage(message: Message) = DB.asyncIO {
        database.insert(Messages) {
            with(message) {
                set(Messages.content, content)
                set(Messages.publishTime, publishTime)
                set(Messages.maxVisibleBuildNumber, maxVisibleBuildNumber)
                set(Messages.minVisibleBuildNumber, minVisibleBuildNumber)
            }
        }
    }

    suspend fun deleteMessageById(id: Int) = DB.asyncIO {
        database.delete(Messages) {
            it.id eq id
        }
    }

    suspend fun deleteAllMessages() = DB.asyncIO {
        database.deleteAll(Messages)
    }
}