package org.example.kforgepro.modules.user.data

import setValue
import java.io.Serializable
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import javax.inject.Inject


data class User(
    val id: Int, 
    val is_disabled: Boolean, 
    val login_type: Int
) : Serializable {
    companion object {
        private const val MIN_PASSWORD_LENGTH = 7

        fun isValidUsername(username: String): Boolean {
            return username.matches(Regex("^[\\p{L}][\\p{L}\\p{N} _]{1,48}[\\p{L}\\p{N}]$"))
        }

        fun isValidPasswordLength(password: String): Boolean {
            return password.length >= MIN_PASSWORD_LENGTH
        }
    }
}


interface UserDbh {
    @Throws(SQLException::class)
    fun createNew(dbc: Connection, is_disabled: Boolean, login_type: Int) : User

    @Throws(SQLException::class)
    fun loadById(dbc: Connection, id: Int) : User?

    @Throws(SQLException::class)
    fun loadAll(dbc: Connection) : List<User>

    @Throws(SQLException::class)
    fun update(dbc: Connection, obj: User) : Boolean

    @Throws(SQLException::class)
    fun loadPageIdGreater(dbc: Connection, idGreater: Int, pageSize: Int): List<User>

    @Throws(SQLException::class)
    fun loadPageIdLower(dbc: Connection, idLower: Int, pageSize: Int): List<User>

    @Throws(SQLException::class)
    fun loadLastPage(dbc: Connection, pageSize: Int): List<User>

    @Throws(SQLException::class)
    fun count(dbc: Connection) : Int

    @Throws(SQLException::class)
    fun delete(dbc: Connection, id: Int) : Int

    @Throws(SQLException::class)
    fun deleteAll(dbc: Connection) : Int
}

class UserDbhImpl @Inject constructor() : UserDbh {
    private val SQL_INSERT = """INSERT INTO "kforge_propack"."users" ("is_disabled", "login_type") VALUES (?, ?)"""
    private val SQL_SELECT_BY_ID = """SELECT "is_disabled", "login_type" FROM "kforge_propack"."users" WHERE id = ?"""
    private val SQL_SELECT_ALL = """SELECT "id", "is_disabled", "login_type" FROM "kforge_propack"."users""""
    private val SQL_UPDATE = """UPDATE "kforge_propack"."users" SET "is_disabled" = ?, "login_type" = ? WHERE id = ?"""
    private val SQL_SELECT_ID_GREATER = """SELECT "id", "is_disabled", "login_type" FROM "kforge_propack"."users" WHERE id > ?"""
    private val SQL_SELECT_ID_LOWER = """SELECT "id", "is_disabled", "login_type" FROM "kforge_propack"."users" WHERE id < ?"""
    private val SQL_SELECT_LAST = """SELECT "id", "is_disabled", "login_type" FROM "kforge_propack"."users" ORDER BY id DESC"""
    private val SQL_COUNT = """SELECT COUNT(id) FROM "kforge_propack"."users""""
    private val SQL_DELETE = """DELETE FROM "kforge_propack"."users" WHERE id = ?"""
    private val SQL_DELETE_ALL = """DELETE FROM "kforge_propack"."users""""

    @Throws(SQLException::class)
    override fun createNew(dbc: Connection, is_disabled: Boolean, login_type: Int) : User {
        dbc.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS).use {
            it.setValue(1, is_disabled)
            it.setValue(2, login_type)

            it.executeUpdate()
            it.generatedKeys.use {
                it.next()
                return User(
                    it.getInt(1),
                    is_disabled, 
                    login_type
                )
            }
        }
    }


    @Throws(SQLException::class)
    override fun loadById(dbc: Connection, id: Int) : User? {
        dbc.prepareStatement(SQL_SELECT_BY_ID).use {
            it.setValue(1, id)

            it.executeQuery().use {
                return if (it.next()) {
                    User(
                        id,
                        it.getBoolean(1), 
                        it.getInt(2)
                    )
                } else {
                    null
                }
            }
        }
    }

    @Throws(SQLException::class)
    override fun loadAll(dbc: Connection) : List<User> {
        val ret = ArrayList<User>()
        dbc.prepareStatement(SQL_SELECT_ALL).use {
            it.executeQuery().use {
                while (it.next()) {
                    ret.add(
                        User(
                        it.getInt(1), 
                        it.getBoolean(2), 
                        it.getInt(3)
                    )
                    )
                }
            }
        }

        return ret
    }

    @Throws(SQLException::class)
    override fun update(dbc: Connection, obj: User) : Boolean {
        dbc.prepareStatement(SQL_UPDATE).use {
            it.setValue(1, obj.is_disabled)
            it.setValue(2, obj.login_type)
            it.setValue(3, obj.id)

            return it.executeUpdate() > 0
        }
    }

    @Throws(SQLException::class)
    override fun loadPageIdGreater(dbc: Connection, idGreater: Int, pageSize: Int): List<User> {
        if (pageSize <= 0) {
            throw IllegalArgumentException("pageSize <= 0")
        }

        dbc.prepareStatement(SQL_SELECT_ID_GREATER).use {
            it.setValue(1, idGreater)

            it.executeQuery().use {
                var count = 0
                val ret = ArrayList<User>()

                while (it.next()) {
                    ret.add(
                        User(
                        it.getInt(1), 
                        it.getBoolean(2), 
                        it.getInt(3)
                    )
                    )
                    count++
                    if (count == pageSize) {
                        return ret
                    }
                }

                return ret
            }
        }
    }


    @Throws(SQLException::class)
    override fun loadPageIdLower(dbc: Connection, idLower: Int, pageSize: Int): List<User> {
        if (pageSize <= 0) {
            throw IllegalArgumentException("pageSize <= 0")
        }

        dbc.prepareStatement(SQL_SELECT_ID_LOWER).use {
            it.setValue(1, idLower)

            it.executeQuery().use {
                var count = 0
                val ret = ArrayList<User>()

                while (it.next()) {
                    ret.add(
                        User(
                        it.getInt(1), 
                        it.getBoolean(2), 
                        it.getInt(3)
                    )
                    )
                    count++
                    if (count == pageSize) {
                        return ret
                    }
                }

                return ret
            }
        }
    }

    @Throws(SQLException::class)
    override fun loadLastPage(dbc: Connection, pageSize: Int): List<User> {
        val ret = ArrayList<User>()
        dbc.prepareStatement(SQL_SELECT_LAST).use {
            it.executeQuery().use {
                var count = 0
                while (it.next()) {
                    ret.add(
                        User(
                        it.getInt(1), 
                        it.getBoolean(2), 
                        it.getInt(3)
                    )
                    )
                    count++
                    if (count == pageSize) {
                        return ret
                    }
                }
            }
        }

        return ret
    }

    @Throws(SQLException::class)
    override fun count(dbc: Connection) : Int {
        dbc.prepareStatement(SQL_COUNT).use {
            it.executeQuery().use {
                it.next()
                return it.getInt(1)
            }
        }
    }

    @Throws(SQLException::class)
    override fun delete(dbc: Connection, id: Int) : Int {
        dbc.prepareStatement(SQL_DELETE).use {
            it.setValue(1, id)
            return it.executeUpdate()
        }
    }

    @Throws(SQLException::class)
    override fun deleteAll(dbc: Connection) : Int {
        dbc.prepareStatement(SQL_DELETE_ALL).use {
            return it.executeUpdate()
        }
    }
}


enum class LoginType(private val _code: Int) {
    NATIVE(0),
    GOOGLE(1),
    FACEBOOK(2),
    TWITTER(3);

    fun getId(): Int {
        return _code
    }
}
