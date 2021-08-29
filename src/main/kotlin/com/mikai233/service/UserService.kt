@file:Suppress("unused", "MemberVisibilityCanBePrivate", "DuplicatedCode")

package com.mikai233.service

import com.mikai233.orm.DB
import com.mikai233.orm.User
import com.mikai233.orm.Users
import com.mikai233.tool.asyncIO
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/22
 */

class UserService {
    private val passwordEncoder = BCryptPasswordEncoder()
    fun matches(rawPassword: CharSequence, encodedPassword: String) =
        passwordEncoder.matches(rawPassword, encodedPassword)

    fun encode(rawPassword: CharSequence): String = passwordEncoder.encode(rawPassword)

    suspend fun getUsersByName(name: String) = DB.asyncIO {
        users.filter { it.username eq name }.toList()
    }

    suspend fun getUserById(id: Int) = DB.asyncIO {
        users.find { it.id eq id }
    }

    suspend fun getUsersByPage(page: Int, size: Int) = DB.asyncIO {
        require(page >= 0) { "page: $page must >= 0" }
        require(size >= 0) { "size: $size must >= 0" }
        users.drop(page * size).take(size).toList()
    }

    suspend fun createUser(user: User) = DB.asyncIO {
        database.insert(Users) {
            with(user) {
                set(Users.username, username)
                set(Users.password, encode(password))
                set(Users.roles, roles.joinToString(","))
            }
        }
    }

    suspend fun updateUser(user: User) = DB.asyncIO {
        database.update(Users) {
            with(user) {
                where { it.id eq id }
                set(Users.username, username)
                set(Users.password, encode(password))
                set(Users.roles, roles.joinToString(","))
            }
        }
    }

    suspend fun deleteUserById(id: Int) = DB.asyncIO {
        database.delete(Users) {
            it.id eq id
        }
    }

    suspend fun deleteUsersByName(name: String) = DB.asyncIO {
        database.delete(Users) {
            it.username eq name
        }
    }

    suspend fun deleteAllUsers() = DB.asyncIO {
        database.deleteAll(Users)
    }
}