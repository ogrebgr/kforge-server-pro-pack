package org.example.kforgepro.modules.user.data

import org.example.kforgepro.dagger.UserBlowfishAuto
import org.example.kforgepro.dagger.UserBlowfishFinal
import org.mindrot.jbcrypt.BCrypt
import java.sql.Connection
import java.sql.Connection.TRANSACTION_SERIALIZABLE
import java.sql.SQLException
import javax.inject.Inject

interface UserDbOps {
    @Throws(SQLException::class)
    fun createNewNamedUser(
        dbc: Connection,
        username: String,
        passwordClearForm: String,
        screenName: String
    ): NewUserResult

    @Throws(SQLException::class)
    fun createNewAuto(
        dbc: Connection,
        username: String,
        passwordClearForm: String
    ): NewUserResult

    @Throws(SQLException::class)
    fun createNewNamedUserPostAuto(
        dbc: Connection,
        username: String,
        passwordClearForm: String,
        screenName: String?
    ): NewUserResult
}

sealed class NewUserResult

data class NewUserResultError(val isUsernameTaken: Boolean, val isScreenNameTaken: Boolean) : NewUserResult()

data class NewUserResultOK(val user: User) : NewUserResult()

class UserDbOpsImpl @Inject constructor(
    private val userDbh: UserDbh,
    @UserBlowfishAuto private val blowfishDbhAuto: UserBlowfishDbh,
    @UserBlowfishFinal private val blowfishDbhFinal: UserBlowfishDbh,
    private val screenNameDbh: UserScreenNameDbh
) : UserDbOps {

    override fun createNewNamedUser(
        dbc: Connection,
        username: String,
        passwordClearForm: String,
        screenName: String
    ): NewUserResult {
        try {
            dbc.autoCommit = false
            dbc.transactionIsolation = TRANSACTION_SERIALIZABLE
            if (blowfishDbhAuto.usernameExists(dbc, username) || blowfishDbhFinal.usernameExists(dbc, username)) {
                return NewUserResultError(isUsernameTaken = true, isScreenNameTaken = false)
            }

            if (screenNameDbh.isScreenNameTaken(dbc, screenName)) {
                return NewUserResultError(isUsernameTaken = false, isScreenNameTaken = true)
            }

            val user = userDbh.createNew(dbc, false, LoginType.NATIVE.getId())
            blowfishDbhAuto.createNew(dbc, user.id, username, BCrypt.hashpw(passwordClearForm, BCrypt.gensalt()))
            screenNameDbh.createNew(dbc, user.id, screenName)
            dbc.commit()

            return NewUserResultOK(user)
        } catch (e: Exception) {
            dbc.rollback()
            throw e
        } finally {
            dbc.autoCommit = true
        }
    }

    override fun createNewAuto(dbc: Connection, username: String, passwordClearForm: String): NewUserResult {
        try {
            dbc.autoCommit = false
            dbc.transactionIsolation = TRANSACTION_SERIALIZABLE

            if (blowfishDbhAuto.usernameExists(dbc, username) || blowfishDbhFinal.usernameExists(dbc, username)) {
                return NewUserResultError(isUsernameTaken = true, isScreenNameTaken = false)
            }
            val user = userDbh.createNew(dbc, false, LoginType.NATIVE.getId())
            blowfishDbhAuto.createNew(dbc, user.id, username, BCrypt.hashpw(passwordClearForm, BCrypt.gensalt()))

            dbc.commit()

            return NewUserResultOK(user)
        } catch (e: Exception) {
            dbc.rollback()
            throw e
        } finally {
            dbc.autoCommit = true
        }
    }

    override fun createNewNamedUserPostAuto(
        dbc: Connection,
        username: String,
        passwordClearForm: String,
        screenName: String?
    ): NewUserResult {

        try {
            dbc.autoCommit = false
            dbc.transactionIsolation = TRANSACTION_SERIALIZABLE
            if (blowfishDbhAuto.usernameExists(dbc, username) || blowfishDbhFinal.usernameExists(dbc, username)) {
                return NewUserResultError(isUsernameTaken = true, isScreenNameTaken = false)
            }

            if (screenName != null) {
                if (screenNameDbh.isScreenNameTaken(dbc, screenName)) {
                    return NewUserResultError(isUsernameTaken = false, isScreenNameTaken = true)
                }
            }

            val user = userDbh.createNew(dbc, false, LoginType.NATIVE.getId())
            blowfishDbhFinal.createNew(dbc, user.id, username, BCrypt.hashpw(passwordClearForm, BCrypt.gensalt()))
            blowfishDbhAuto.deleteByUser(dbc, user.id)
            if (screenName != null) {
                screenNameDbh.createNew(dbc, user.id, screenName)
            }
            dbc.commit()

            return NewUserResultOK(user)
        } catch (e: java.lang.Exception) {
            dbc.rollback()
            throw e
        } finally {
            dbc.autoCommit = true
        }
    }
}