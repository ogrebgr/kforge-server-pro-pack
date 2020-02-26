package org.example.kforgepro.modules.admin.data

import com.bolyartech.scram_sasl.common.ScramUtils
import setValue
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import javax.inject.Inject


data class AdminUserScram(
    val id: Int,
    val user: Int,
    val username: String,
    val salt: ByteArray,
    val server_key: ByteArray,
    val stored_key: ByteArray,
    val iterations: Int
)

// TODO add provider to Dagger DI
interface AdminUserScramDbh {
    @Throws(SQLException::class)
    fun createNew(
        dbc: Connection,
        adminUserDbh: AdminUserDbh,
        scramDbh: ScramDbh,
        isSuperAdmin: Boolean,
        name: String,
        username: String,
        data: ScramUtils.NewPasswordByteArrayData
    ): AdminUser?

    @Throws(SQLException::class)
    fun createNew(
        dbc: Connection,
        user: Int,
        username: String,
        salt: ByteArray,
        server_key: ByteArray,
        stored_key: ByteArray,
        iterations: Int
    ): AdminUserScram

    @Throws(SQLException::class)
    fun loadById(dbc: Connection, id: Int): AdminUserScram?

    @Throws(SQLException::class)
    fun loadAll(dbc: Connection): List<AdminUserScram>

    @Throws(SQLException::class)
    fun update(dbc: Connection, obj: AdminUserScram): Boolean

    @Throws(SQLException::class)
    fun loadPageIdGreater(dbc: Connection, idGreater: Int, pageSize: Int): List<AdminUserScram>

    @Throws(SQLException::class)
    fun loadPageIdLower(dbc: Connection, idLower: Int, pageSize: Int): List<AdminUserScram>

    @Throws(SQLException::class)
    fun loadLastPage(dbc: Connection, pageSize: Int): List<AdminUserScram>

    @Throws(SQLException::class)
    fun count(dbc: Connection): Int

    @Throws(SQLException::class)
    fun delete(dbc: Connection, id: Int): Int

    @Throws(SQLException::class)
    fun deleteAll(dbc: Connection): Int
}

class AdminUserScramDbhImpl @Inject constructor() :
    AdminUserScramDbh {
    private val SQL_INSERT =
        """INSERT INTO "kforge_propack"."admin_user_scram" ("user", "username", "salt", "server_key", "stored_key", "iterations", "username_lc") VALUES (?, ?, ?, ?, ?, ?, ?)"""
    private val SQL_SELECT_BY_ID =
        """SELECT "user", "username", "salt", "server_key", "stored_key", "iterations", "username_lc" FROM "kforge_propack"."admin_user_scram" WHERE id = ?"""
    private val SQL_SELECT_ALL =
        """SELECT "id", "user", "username", "salt", "server_key", "stored_key", "iterations", "username_lc" FROM "kforge_propack"."admin_user_scram""""
    private val SQL_UPDATE =
        """UPDATE "kforge_propack"."admin_user_scram" SET "user" = ?, "username" = ?, "salt" = ?, "server_key" = ?, "stored_key" = ?, "iterations" = ?, "username_lc" = ? WHERE id = ?"""
    private val SQL_SELECT_ID_GREATER =
        """SELECT "id", "user", "username", "salt", "server_key", "stored_key", "iterations", "username_lc" FROM "kforge_propack"."admin_user_scram" WHERE id > ?"""
    private val SQL_SELECT_ID_LOWER =
        """SELECT "id", "user", "username", "salt", "server_key", "stored_key", "iterations", "username_lc" FROM "kforge_propack"."admin_user_scram" WHERE id < ?"""
    private val SQL_SELECT_LAST =
        """SELECT "id", "user", "username", "salt", "server_key", "stored_key", "iterations", "username_lc" FROM "kforge_propack"."admin_user_scram" ORDER BY id DESC"""
    private val SQL_COUNT = """SELECT COUNT(id) FROM "kforge_propack"."admin_user_scram""""
    private val SQL_DELETE = """DELETE FROM "kforge_propack"."admin_user_scram" WHERE id = ?"""
    private val SQL_DELETE_ALL = """DELETE FROM "kforge_propack"."admin_user_scram""""


    override fun createNew(
        dbc: Connection,
        adminUserDbh: AdminUserDbh,
        scramDbh: ScramDbh,
        isSuperAdmin: Boolean,
        name: String,
        username: String,
        data: ScramUtils.NewPasswordByteArrayData
    ): AdminUser {
        return try {
            dbc.autoCommit = false
            dbc.transactionIsolation = Connection.TRANSACTION_SERIALIZABLE

            val user = adminUserDbh.createNew(dbc, false, isSuperAdmin, name)
            scramDbh.createNew(dbc, user.id, username, data)
            dbc.commit()
            user
        } catch (e: SQLException) {
            dbc.rollback()
            throw e
        } finally {
            dbc.autoCommit = true
        }


    }


    @Throws(SQLException::class)
    override fun createNew(
        dbc: Connection,
        user: Int,
        username: String,
        salt: ByteArray,
        server_key: ByteArray,
        stored_key: ByteArray,
        iterations: Int
    ): AdminUserScram {
        dbc.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS).use {
            it.setValue(1, user)
            it.setValue(2, username)
            it.setValue(3, salt)
            it.setValue(4, server_key)
            it.setValue(5, stored_key)
            it.setValue(6, iterations)
            it.setValue(7, username.toLowerCase())

            it.executeUpdate()
            it.generatedKeys.use {
                it.next()
                return AdminUserScram(
                    it.getInt(1),
                    user,
                    username,
                    salt,
                    server_key,
                    stored_key,
                    iterations
                )
            }
        }
    }


    @Throws(SQLException::class)
    override fun loadById(dbc: Connection, id: Int): AdminUserScram? {
        dbc.prepareStatement(SQL_SELECT_BY_ID).use {
            it.setValue(1, id)

            it.executeQuery().use {
                return if (it.next()) {
                    AdminUserScram(
                        id,
                        it.getInt(1),
                        it.getString(2),
                        it.getBytes(3),
                        it.getBytes(4),
                        it.getBytes(5),
                        it.getInt(6)
                    )
                } else {
                    null
                }
            }
        }
    }

    @Throws(SQLException::class)
    override fun loadAll(dbc: Connection): List<AdminUserScram> {
        val ret = ArrayList<AdminUserScram>()
        dbc.prepareStatement(SQL_SELECT_ALL).use {
            it.executeQuery().use {
                while (it.next()) {
                    ret.add(
                        AdminUserScram(
                            it.getInt(1),
                            it.getInt(2),
                            it.getString(3),
                            it.getBytes(4),
                            it.getBytes(5),
                            it.getBytes(6),
                            it.getInt(7)
                        )
                    )
                }
            }
        }

        return ret
    }

    @Throws(SQLException::class)
    override fun update(dbc: Connection, obj: AdminUserScram): Boolean {
        dbc.prepareStatement(SQL_UPDATE).use {
            it.setValue(1, obj.user)
            it.setValue(2, obj.username)
            it.setValue(3, obj.salt)
            it.setValue(4, obj.server_key)
            it.setValue(5, obj.stored_key)
            it.setValue(6, obj.iterations)
            it.setValue(7, obj.username.toLowerCase())
            it.setValue(8, obj.id)

            return it.executeUpdate() > 0
        }
    }

    @Throws(SQLException::class)
    override fun loadPageIdGreater(dbc: Connection, idGreater: Int, pageSize: Int): List<AdminUserScram> {
        if (pageSize <= 0) {
            throw IllegalArgumentException("pageSize <= 0")
        }

        dbc.prepareStatement(SQL_SELECT_ID_GREATER).use {
            it.setValue(1, idGreater)

            it.executeQuery().use {
                var count = 0
                val ret = ArrayList<AdminUserScram>()

                while (it.next()) {
                    ret.add(
                        AdminUserScram(
                            it.getInt(1),
                            it.getInt(2),
                            it.getString(3),
                            it.getBytes(4),
                            it.getBytes(5),
                            it.getBytes(6),
                            it.getInt(7)
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
    override fun loadPageIdLower(dbc: Connection, idLower: Int, pageSize: Int): List<AdminUserScram> {
        if (pageSize <= 0) {
            throw IllegalArgumentException("pageSize <= 0")
        }

        dbc.prepareStatement(SQL_SELECT_ID_LOWER).use {
            it.setValue(1, idLower)

            it.executeQuery().use {
                var count = 0
                val ret = ArrayList<AdminUserScram>()

                while (it.next()) {
                    ret.add(
                        AdminUserScram(
                            it.getInt(1),
                            it.getInt(2),
                            it.getString(3),
                            it.getBytes(4),
                            it.getBytes(5),
                            it.getBytes(6),
                            it.getInt(7)
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
    override fun loadLastPage(dbc: Connection, pageSize: Int): List<AdminUserScram> {
        val ret = ArrayList<AdminUserScram>()
        dbc.prepareStatement(SQL_SELECT_LAST).use {
            it.executeQuery().use {
                var count = 0
                while (it.next()) {
                    ret.add(
                        AdminUserScram(
                            it.getInt(1),
                            it.getInt(2),
                            it.getString(3),
                            it.getBytes(4),
                            it.getBytes(5),
                            it.getBytes(6),
                            it.getInt(7)
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