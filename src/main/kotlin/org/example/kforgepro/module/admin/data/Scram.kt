package org.example.kforgepro.module.admin.data

import com.bolyartech.scram_sasl.common.ScramUtils
import com.google.common.base.Strings
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.sql.Connection
import java.sql.SQLException


data class Scram(
    private val user: Int,
    private val username: String,
    private val salt: ByteArray,
    private val serverKey: ByteArray,
    private val storedKey: ByteArray,
    private val iterations: Int
) {

    init {
        require(user > 0) { "user <= 0: $user" }
        require(!Strings.isNullOrEmpty(username)) { "username is empty" }
        require(iterations > 0) { "iterations <= 0" }
        require(isValidUsername(username)) { "Invalid username: $username" }
    }

    companion object {
        private const val DEFAULT_ITERATIONS = 4096
        private const val DEFAULT_HMAC = "HmacSHA512"
        private const val DEFAULT_DIGEST = "SHA-512"

        fun isValidUsername(username: String): Boolean {
            return username.matches(Regex("^[\\p{L}][\\p{L}\\p{N} _]{1,48}[\\p{L}\\p{N}]$"))
        }

        @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
        fun createPasswordData(password: String): ScramUtils.NewPasswordByteArrayData {
            val random = SecureRandom()
            val salt = ByteArray(24)
            random.nextBytes(salt)

            return ScramUtils.newPassword(
                password,
                salt,
                DEFAULT_ITERATIONS,
                DEFAULT_DIGEST,
                DEFAULT_HMAC
            )
        }
    }
}


interface ScramDbh {
    @Throws(SQLException::class)
    fun loadByUser(dbc: Connection, user: Int): Scram?

    @Throws(SQLException::class)
    fun loadByUsername(dbc: Connection, username: String): Scram?

    @Throws(SQLException::class)
    fun usernameExists(dbc: Connection, username: String): Boolean

    @Throws(SQLException::class)
    fun createNew(
        dbc: Connection,
        user: Int,
        username: String,
        passwordData: ScramUtils.NewPasswordByteArrayData
    ): Scram?

    @Throws(SQLException::class)
    fun replace(
        dbc: Connection,
        userId: Long,
        username: String,
        passwordData: ScramUtils.NewPasswordByteArrayData
    ): Scram

    @Throws(SQLException::class)
    fun changePassword(dbc: Connection, userId: Long, passwordData: ScramUtils.NewPasswordByteArrayData): Boolean
}


class ScramDbhImpl(private val tableName: String) :
    ScramDbh {

    override fun loadByUser(dbc: Connection, user: Int): Scram? {
        val sql = "SELECT username, salt, server_key, stored_key, iterations " +
                "FROM " + tableName +
                " WHERE user = ?"
        dbc.prepareStatement(sql).use { psLoad ->
            psLoad.setInt(1, user)
            psLoad.executeQuery().use { rs ->
                return if (rs.next()) {
                    Scram(
                        user,
                        rs.getString(1),
                        rs.getBytes(2),
                        rs.getBytes(3),
                        rs.getBytes(4),
                        rs.getInt(5)
                    )
                } else {
                    null
                }
            }
        }
    }

    override fun loadByUsername(dbc: Connection, username: String): Scram? {
        val sql = "SELECT user, salt, server_key, stored_key, iterations " +
                "FROM " + tableName +
                " WHERE username = ?"
        dbc.prepareStatement(sql).use { psLoad ->
            psLoad.setString(1, username)
            psLoad.executeQuery().use { rs ->
                return if (rs.next()) {
                    Scram(
                        rs.getInt(1),
                        username,
                        rs.getBytes(2),
                        rs.getBytes(3),
                        rs.getBytes(4),
                        rs.getInt(5)
                    )
                } else {
                    null
                }
            }
        }
    }

    override fun usernameExists(dbc: Connection, username: String): Boolean {
        val sql = "SELECT user " +
                "FROM " + tableName +
                " WHERE username_lc = ?"
        dbc.prepareStatement(sql).use { psLoad ->
            psLoad.setString(1, username.toLowerCase())
            psLoad.executeQuery().use { rs -> return rs.next() }
        }
    }

    override fun createNew(
        dbc: Connection,
        user: Int,
        username: String,
        passwordData: ScramUtils.NewPasswordByteArrayData
    ): Scram? {
        if (!usernameExists(dbc, username)) {
            val ret = Scram(
                user, username, passwordData.salt, passwordData.serverKey,
                passwordData.storedKey, passwordData.iterations
            )
            val sql = "INSERT INTO " + tableName + " " +
                    """("user", username, salt, server_key, stored_key, iterations, username_lc) VALUES (?,?,?,?,?,?,?)"""
            dbc.prepareStatement(sql).use { psInsert ->
                psInsert.setLong(1, user.toLong())
                psInsert.setString(2, username)
                psInsert.setBytes(3, passwordData.salt)
                psInsert.setBytes(4, passwordData.serverKey)
                psInsert.setBytes(5, passwordData.storedKey)
                psInsert.setInt(6, passwordData.iterations)
                psInsert.setString(7, username.toLowerCase())
                psInsert.executeUpdate()
            }
            return ret
        } else {
            return null
        }
    }

    override fun replace(
        dbc: Connection,
        userId: Long,
        username: String,
        passwordData: ScramUtils.NewPasswordByteArrayData
    ): Scram {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun changePassword(
        dbc: Connection,
        userId: Long,
        passwordData: ScramUtils.NewPasswordByteArrayData
    ): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
