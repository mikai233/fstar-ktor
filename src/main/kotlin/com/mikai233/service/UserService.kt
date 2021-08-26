package com.mikai233.service

import com.mikai233.orm.DB
import com.mikai233.orm.User
import com.mikai233.orm.Users
import com.mikai233.tool.asyncIO
import org.ktorm.dsl.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/22
 */

class UserService {
    val passwordEncoder = BCryptPasswordEncoder()
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