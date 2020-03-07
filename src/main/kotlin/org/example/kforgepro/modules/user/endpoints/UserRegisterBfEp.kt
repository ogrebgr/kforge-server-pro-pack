package org.example.kforgepro.modules.user.endpoints

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.handler.ForgeDbEndpoint
import com.bolyartech.forge.server.misc.Params
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.response.forge.MissingParametersResponse
import com.bolyartech.forge.server.response.forge.OkResponse
import com.bolyartech.forge.server.route.RequestContext
import com.google.gson.Gson
import org.example.kforgepro.modules.user.SessionInfoUser
import org.example.kforgepro.modules.user.UserResponseCodes
import org.example.kforgepro.modules.user.data.*
import java.lang.IllegalStateException
import java.sql.Connection
import javax.inject.Inject

class UserRegisterBfEp @Inject constructor(dbPool: DbPool, private val userDbOps: UserDbOps) : ForgeDbEndpoint(dbPool) {
    private val PARAM_USERNAME = "username"
    private val PARAM_PASSWORD = "password"
    private val PARAM_SCREEN_NAME = "screen_name"

    private val gson = Gson()

    override fun handleForge(ctx: RequestContext, dbc: Connection): ForgeResponse {
        val username = ctx.getFromPost(PARAM_USERNAME)
        val password = ctx.getFromPost(PARAM_PASSWORD)
        val screenName = ctx.getFromPost(PARAM_SCREEN_NAME)

        if (!Params.areAllPresent(username, password, screenName)) {
            return MissingParametersResponse.getInstance()
        }

        if (!User.isValidUsername(username)) {
            return ForgeResponse(UserResponseCodes.INVALID_USERNAME, "Invalid username")
        }

        if (!User.isValidPasswordLength(password)) {
            return ForgeResponse(UserResponseCodes.INVALID_PASSWORD, "Password too short")
        }

        if (!UserScreenName.isValid(screenName)) {
            return ForgeResponse(UserResponseCodes.INVALID_SCREEN_NAME, "Invalid screen name")
        }

        val rez = userDbOps.createNewNamedUser(dbc, username, password, screenName)

        if (rez is NewUserResultOK) {
            return OkResponse(
                gson.toJson(
                    RokLogin(
                        ctx.session.maxInactiveInterval,
                        SessionInfoUser(rez.user.id, if (screenName != null) screenName else null)
                    )
                )
            )
        } else if (rez is NewUserResultError) {
            return if (rez.isUsernameTaken) {
                ForgeResponse(UserResponseCodes.USERNAME_EXISTS, "Invalid Login")
            } else {
                ForgeResponse(UserResponseCodes.SCREEN_NAME_EXISTS, "screen name taken")
            }
        } else {
            throw IllegalStateException()
        }
    }
}