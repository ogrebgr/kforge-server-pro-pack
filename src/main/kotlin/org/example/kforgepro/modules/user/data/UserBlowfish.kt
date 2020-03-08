package org.example.kforgepro.modules.user.data

import setValue
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import javax.inject.Inject


data class UserBlowfish(
    val user: Int,
    val username: String,
    val password: String,
    val username_lc: String
)

interface UserBlowfishDbh {
    @Throws(SQLException::class)
    fun createNew(dbc: Connection, user: Int, username: String, password: String): UserBlowfish

    @Throws(SQLException::class)
    fun loadById(dbc: Connection, id: Int): UserBlowfish?

    @Throws(SQLException::class)
    fun loadByUsername(dbc: Connection, username: String): UserBlowfish?

    @Throws(SQLException::class)
    fun loadAll(dbc: Connection): List<UserBlowfish>

//    @Throws(SQLException::class)
//    fun update(dbc: Connection, obj: UserBlowfish): Boolean

    @Throws(SQLException::class)
    fun count(dbc: Connection): Int

    @Throws(SQLException::class)
    fun delete(dbc: Connection, id: Int): Int

    @Throws(SQLException::class)
    fun deleteByUser(dbc: Connection, userId: Int): Int

    @Throws(SQLException::class)
    fun usernameExists(dbc: Connection, username: String): Boolean
}

class UserBlowfishDbhImpl @Inject constructor(private val tableName: String) : UserBlowfishDbh {
    private val SQL_INSERT =
        """INSERT INTO "kforge_propack"."$tableName" ("user", "username", "password", "username_lc") VALUES (?, ?, ?, ?)"""
    private val SQL_SELECT_BY_ID =
        """SELECT "user", "username", "password", "username_lc" FROM "kforge_propack"."$tableName" WHERE id = ?"""
    private val SQL_SELECT_BY_USERNAME =
        """SELECT "user", "password" FROM "kforge_propack"."$tableName" WHERE username = ?"""
    private val SQL_SELECT_ALL =
        """SELECT "user", "username", "password", "username_lc" FROM "kforge_propack"."$tableName""""
    private val SQL_COUNT = """SELECT COUNT(id) FROM "kforge_propack"."$tableName""""
    private val SQL_DELETE = """DELETE FROM "kforge_propack"."$tableName" WHERE id = ?"""
    private val SQL_DELETE_BY_USER = """DELETE FROM "kforge_propack"."$tableName" WHERE "user" = ?"""

    @Throws(SQLException::class)
    override fun createNew(
        dbc: Connection,
        user: Int,
        username: String,
        password: String
    ): UserBlowfish {
        dbc.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS).use {
            it.setValue(1, user)
            it.setValue(2, username)
            it.setValue(3, password)
            it.setValue(4, username.toLowerCase())

            it.executeUpdate()
            it.generatedKeys.use {
                it.next()
                return UserBlowfish(
                    user,
                    username,
                    password,
                    username.toLowerCase()
                )
            }
        }
    }


    @Throws(SQLException::class)
    override fun loadById(dbc: Connection, id: Int): UserBlowfish? {
        dbc.prepareStatement(SQL_SELECT_BY_ID).use {
            it.setValue(1, id)

            it.executeQuery().use {
                return if (it.next()) {
                    UserBlowfish(
                        it.getInt(1),
                        it.getString(2),
                        it.getString(3),
                        it.getString(4)
                    )
                } else {
                    null
                }
            }
        }
    }

    override fun loadByUsername(dbc: Connection, username: String): UserBlowfish? {
        dbc.prepareStatement(SQL_SELECT_BY_USERNAME).use {
            it.setValue(1, username)

            it.executeQuery().use {
                return if (it.next()) {
                    UserBlowfish(
                        it.getInt(1),
                        username,
                        it.getString(2),
                        username.toLowerCase()
                    )
                } else {
                    null
                }
            }
        }
    }

    @Throws(SQLException::class)
    override fun loadAll(dbc: Connection): List<UserBlowfish> {
        val ret = ArrayList<UserBlowfish>()
        dbc.prepareStatement(SQL_SELECT_ALL).use {
            it.executeQuery().use {
                while (it.next()) {
                    ret.add(
                        UserBlowfish(
                            it.getInt(1),
                            it.getString(2),
                            it.getString(3),
                            it.getString(4)
                        )
                    )
                }
            }
        }

        return ret
    }

//    @Throws(SQLException::class)
//    override fun update(dbc: Connection, obj: UserBlowfish): Boolean {
//        dbc.prepareStatement(SQL_UPDATE).use {
//            it.setValue(0, obj.user)
//            it.setValue(1, obj.username)
//            it.setValue(2, obj.password)
//            it.setValue(3, obj.username_lc)
//            it.setValue(4, obj.id)
//
//            return it.executeUpdate() > 0
//        }
//    }

    @Throws(SQLException::class)
    override fun count(dbc: Connection): Int {
        dbc.prepareStatement(SQL_COUNT).use {
            it.executeQuery().use {
                it.next()
                return it.getInt(1)
            }
        }
    }

    @Throws(SQLException::class)
    override fun delete(dbc: Connection, id: Int): Int {
        dbc.prepareStatement(SQL_DELETE).use {
            it.setValue(1, id)
            return it.executeUpdate()
        }
    }

    override fun deleteByUser(dbc: Connection, userId: Int): Int {
        dbc.prepareStatement(SQL_DELETE_BY_USER).use {
            it.setValue(1, userId)
            return it.executeUpdate()
        }
    }

    override fun usernameExists(dbc: Connection, username: String): Boolean {
        return loadByUsername(dbc, username) != null
    }
}