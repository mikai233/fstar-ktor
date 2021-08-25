package com.mikai233.service

import com.mikai233.orm.DB
import com.mikai233.orm.User
import com.mikai233.orm.Users
import com.mikai233.tool.asyncIO
import org.ktorm.dsl.*

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/22
 */

class UserService {
    suspend fun getUserByName(name: String): List<User> {
        return DB.asyncIO {
            database.from(Users).select().where { Users.username eq name }.map { Users.createEntity(it) }
        }
    }

    suspend fun createUser(user: User) {
        return DB.asyncIO {

        }
    }
}