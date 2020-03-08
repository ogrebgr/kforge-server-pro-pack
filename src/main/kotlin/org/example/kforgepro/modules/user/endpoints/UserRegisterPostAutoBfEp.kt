package org.example.kforgepro.modules.user.endpoints

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.misc.Params
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.response.forge.MissingParametersResponse
import com.bolyartech.forge.server.response.forge.OkResponse
import com.bolyartech.forge.server.route.RequestContext
import org.example.kforgepro.modules.user.UserResponseCodes
import org.example.kforgepro.modules.user.data.*
import java.lang.IllegalStateException
import java.sql.Connection
import javax.inject.Inject

class UserRegisterPostAutoBfEp @Inject constructor(
    dbPool: DbPool,
    private val userDbOps: UserDbOps,
    private val userScreenNameDbh: UserScreenNameDbh
) : UserDbEndpoint(dbPool) {

    private val PARAM_USERNAME = "username"
    private val PARAM_PASSWORD = "password"
    private val PARAM_SCREEN_NAME = "screen_name"

    override fun handle(ctx: RequestContext, dbc: Connection, user: User): ForgeResponse {
        val username = ctx.getFromPost(PARAM_USERNAME)
        val password = ctx.getFromPost(PARAM_PASSWORD)
        val screenName = ctx.getFromPost(PARAM_SCREEN_NAME)

        if (!Params.areAllPresent(username, password)) {
            return MissingParametersResponse.getInstance()
        }

        if (!User.isValidUsername(username)) {
            return ForgeResponse(UserResponseCodes.INVALID_USERNAME, "Invalid username")
        }

        if (!User.isValidPasswordLength(password)) {
            return ForgeResponse(UserResponseCodes.INVALID_PASSWORD, "Password too short")
        }

        val existing = userScreenNameDbh.loadByUser(dbc, user.id)
        if (existing == null) {
            if (screenName == null || screenName.isEmpty()) {
                return MissingParametersResponse.getInstance()
            }

            if (!UserScreenName.isValid(screenName)) {
                return ForgeResponse(UserResponseCodes.INVALID_SCREEN_NAME, "Invalid screen name")
            }
        }

        val rez = userDbOps.createNewNamedUserPostAuto(dbc, username, password, screenName)

        if (rez is NewUserResultOK) {
            return OkResponse()
        } else if (rez is NewUserResultError) {
            return if (rez.isUsernameTaken) {
                ForgeResponse(UserResponseCodes.USERNAME_EXISTS, "Username exists")
            } else {
                ForgeResponse(UserResponseCodes.SCREEN_NAME_EXISTS, "screen name taken")
            }
        } else {
            throw IllegalStateException()
        }
    }
}