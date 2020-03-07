package org.example.kforgepro.modules.user.data

import setValue
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import javax.inject.Inject


data class UserScreenName(
    val user: Int,
    val screen_name: String,
    val screen_name_lc: String
) {
    companion object {

        fun isValid(screenName: String): Boolean {
            return screenName != null && screenName.matches(Regex("^[\\p{L}][\\p{L}\\p{N} ]{1,33}[\\p{L}\\p{N}]$"))
        }
    }
}

interface UserScreenNameDbh {
    @Throws(SQLException::class)
    fun createNew(dbc: Connection, user: Int, screenName: String): UserScreenName

    @Throws(SQLException::class)
    fun loadById(dbc: Connection, id: Int): UserScreenName?

    @Throws(SQLException::class)
    fun loadByUser(dbc: Connection, user: Int): UserScreenName?

    @Throws(SQLException::class)
    fun loadAll(dbc: Connection): List<UserScreenName>

    @Throws(SQLException::class)
    fun isScreenNameTaken(dbc: Connection, screenName: String): Boolean

//    @Throws(SQLException::class)
//    fun update(dbc: Connection, obj: UserScreenName) : Boolean

    @Throws(SQLException::class)
    fun loadPageIdGreater(dbc: Connection, idGreater: Int, pageSize: Int): List<UserScreenName>

    @Throws(SQLException::class)
    fun loadPageIdLower(dbc: Connection, idLower: Int, pageSize: Int): List<UserScreenName>

    @Throws(SQLException::class)
    fun loadLastPage(dbc: Connection, pageSize: Int): List<UserScreenName>

    @Throws(SQLException::class)
    fun count(dbc: Connection): Int

    @Throws(SQLException::class)
    fun delete(dbc: Connection, id: Int): Int

    @Throws(SQLException::class)
    fun deleteAll(dbc: Connection): Int
}

class UserScreenNameDbhImpl @Inject constructor() : UserScreenNameDbh {
    private val SQL_INSERT =
        """INSERT INTO "kforge_propack"."user_screen_names" ("user", "screen_name", "screen_name_lc") VALUES (?, ?, ?)"""
    private val SQL_SELECT_BY_ID =
        """SELECT "user", "screen_name", "screen_name_lc" FROM "kforge_propack"."user_screen_names" WHERE id = ?"""
    private val SQL_SELECT_BY_USER =
        """SELECT "screen_name", "screen_name_lc" FROM "kforge_propack"."user_screen_names" WHERE "user" = ?"""
    private val SQL_SELECT_BY_SCREEN_NAME =
        """SELECT "user" FROM "kforge_propack"."user_screen_names" WHERE screen_name_lc = ?"""
    private val SQL_SELECT_ALL =
        """SELECT "user", "screen_name", "screen_name_lc" FROM "kforge_propack"."user_screen_names""""
    private val SQL_UPDATE =
        """UPDATE "kforge_propack"."user_screen_names" SET "user" = ?, "screen_name" = ?, "screen_name_lc" = ? WHERE id = ?"""
    private val SQL_SELECT_ID_GREATER =
        """SELECT "user", "screen_name", "screen_name_lc" FROM "kforge_propack"."user_screen_names" WHERE id > ?"""
    private val SQL_SELECT_ID_LOWER =
        """SELECT "user", "screen_name", "screen_name_lc" FROM "kforge_propack"."user_screen_names" WHERE id < ?"""
    private val SQL_SELECT_LAST =
        """SELECT "user", "screen_name", "screen_name_lc" FROM "kforge_propack"."user_screen_names" ORDER BY id DESC"""
    private val SQL_COUNT = """SELECT COUNT(id) FROM "kforge_propack"."user_screen_names""""
    private val SQL_DELETE = """DELETE FROM "kforge_propack"."user_screen_names" WHERE id = ?"""
    private val SQL_DELETE_ALL = """DELETE FROM "kforge_propack"."user_screen_names""""

    @Throws(SQLException::class)
    override fun createNew(dbc: Connection, user: Int, screenName: String): UserScreenName {
        dbc.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS).use {
            it.setValue(1, user)
            it.setValue(2, screenName)
            it.setValue(3, screenName.toLowerCase())

            it.executeUpdate()
            it.generatedKeys.use {
                it.next()
                return UserScreenName(
                    user,
                    screenName,
                    screenName.toLowerCase()
                )
            }
        }
    }


    @Throws(SQLException::class)
    override fun loadById(dbc: Connection, id: Int): UserScreenName? {
        dbc.prepareStatement(SQL_SELECT_BY_ID).use {
            it.setValue(1, id)

            it.executeQuery().use {
                return if (it.next()) {
                    UserScreenName(
                        it.getInt(0),
                        it.getString(1),
                        it.getString(2)
                    )
                } else {
                    null
                }
            }
        }
    }

    override fun loadByUser(dbc: Connection, user: Int): UserScreenName? {
        dbc.prepareStatement(SQL_SELECT_BY_USER).use {
            it.setValue(1, user)

            it.executeQuery().use {
                return if (it.next()) {
                    UserScreenName(
                        user,
                        it.getString(1),
                        it.getString(2)
                    )
                } else {
                    null
                }
            }
        }
    }

    @Throws(SQLException::class)
    override fun loadAll(dbc: Connection): List<UserScreenName> {
        val ret = ArrayList<UserScreenName>()
        dbc.prepareStatement(SQL_SELECT_ALL).use {
            it.executeQuery().use {
                while (it.next()) {
                    ret.add(
                        UserScreenName(
                            it.getInt(1),
                            it.getString(2),
                            it.getString(3)
                        )
                    )
                }
            }
        }

        return ret
    }

    override fun isScreenNameTaken(dbc: Connection, screenName: String): Boolean {
        dbc.prepareStatement(SQL_SELECT_BY_SCREEN_NAME).use {
            it.setValue(1, screenName.toLowerCase())

            it.executeQuery().use {
                return it.next()
            }
        }
    }

//    @Throws(SQLException::class)
//    override fun update(dbc: Connection, obj: UserScreenName) : Boolean {
//        dbc.prepareStatement(SQL_UPDATE).use {
//            it.setValue(0, obj.user)
//            it.setValue(1, obj.screen_name)
//            it.setValue(2, obj.screen_name_lc)
//            it.setValue(3, obj.id)
//
//            return it.executeUpdate() > 0
//        }
//    }

    @Throws(SQLException::class)
    override fun loadPageIdGreater(dbc: Connection, idGreater: Int, pageSize: Int): List<UserScreenName> {
        if (pageSize <= 0) {
            throw IllegalArgumentException("pageSize <= 0")
        }

        dbc.prepareStatement(SQL_SELECT_ID_GREATER).use {
            it.setValue(1, idGreater)

            it.executeQuery().use {
                var count = 0
                val ret = ArrayList<UserScreenName>()

                while (it.next()) {
                    ret.add(
                        UserScreenName(
                            it.getInt(1),
                            it.getString(2),
                            it.getString(3)
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
    override fun loadPageIdLower(dbc: Connection, idLower: Int, pageSize: Int): List<UserScreenName> {
        if (pageSize <= 0) {
            throw IllegalArgumentException("pageSize <= 0")
        }

        dbc.prepareStatement(SQL_SELECT_ID_LOWER).use {
            it.setValue(1, idLower)

            it.executeQuery().use {
                var count = 0
                val ret = ArrayList<UserScreenName>()

                while (it.next()) {
                    ret.add(
                        UserScreenName(
                            it.getInt(1),
                            it.getString(2),
                            it.getString(3)
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
    override fun loadLastPage(dbc: Connection, pageSize: Int): List<UserScreenName> {
        val ret = ArrayList<UserScreenName>()
        dbc.prepareStatement(SQL_SELECT_LAST).use {
            it.executeQuery().use {
                var count = 0
                while (it.next()) {
                    ret.add(
                        UserScreenName(
                            it.getInt(1),
                            it.getString(2),
                            it.getString(3)
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

    @Throws(SQLException::class)
    override fun deleteAll(dbc: Connection): Int {
        dbc.prepareStatement(SQL_DELETE_ALL).use {
            return it.executeUpdate()
        }
    }
}