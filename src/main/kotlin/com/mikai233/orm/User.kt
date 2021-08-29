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

data class User(val id: Int, val username: String, val password: String, val roles: List<String>)

object Users : BaseTable<User>("fstar_user") {
    val id = int("id").primaryKey()
    val username = varchar("username")
    val password = varchar("password")
    val roles = varchar("roles")
    override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean) = User(
        id = row[id] ?: 0,
        username = row[username].orEmpty(),
        password = row[password].orEmpty(),
        roles = row[roles]?.split(",") ?: emptyList()
    )
}