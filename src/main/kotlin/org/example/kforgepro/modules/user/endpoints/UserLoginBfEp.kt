package org.example.kforgepro.modules.user.endpoints

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.handler.ForgeDbEndpoint
import com.bolyartech.forge.server.misc.Params
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.response.forge.MissingParametersResponse
import com.bolyartech.forge.server.response.forge.OkResponse
import com.bolyartech.forge.server.route.RequestContext
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.example.kforgepro.dagger.UserBlowfishAuto
import org.example.kforgepro.dagger.UserBlowfishFinal
import org.example.kforgepro.modules.user.SessionInfoUser
import org.example.kforgepro.modules.user.UserResponseCodes
import org.example.kforgepro.modules.user.data.*
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.LoggerFactory
import java.sql.Connection
import javax.inject.Inject

class UserLoginBfEp @Inject constructor(
    dbPool: DbPool,
    private val userDbh: UserDbh,
    @UserBlowfishFinal private val blowfishDbhFinal: UserBlowfishDbh,
    private val screenNameDbh: UserScreenNameDbh
) : ForgeDbEndpoint(dbPool) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val PAUSE_AFTER_UNSUCCESSFUL_LOGIN_MILLIS = 500L

    private val PARAM_USERNAME = "username"
    private val PARAM_PASSWORD = "password"

    private val gson = Gson();

    override fun handleForge(ctx: RequestContext, dbc: Connection): ForgeResponse {
        val username = ctx.getFromPost(PARAM_USERNAME)
        val password = ctx.getFromPost(PARAM_PASSWORD)

        if (!Params.areAllPresent(username, password)) {
            return MissingParametersResponse.getInstance()
        }

        val bu = blowfishDbhFinal.loadByUsername(dbc, username)
        if (bu == null) {
            Thread.sleep(PAUSE_AFTER_UNSUCCESSFUL_LOGIN_MILLIS)
            return ForgeResponse(UserResponseCodes.INVALID_LOGIN, "Invalid login")
        }

        if (!BCrypt.checkpw(password, bu.password)) {
            Thread.sleep(PAUSE_AFTER_UNSUCCESSFUL_LOGIN_MILLIS)
            return ForgeResponse(UserResponseCodes.INVALID_LOGIN, "Invalid login")
        }

        val user = userDbh.loadById(dbc, bu.user)
        if (user == null) {
            logger.error("Record for user {} exist in user_blowfish but not in users table")
            return ForgeResponse(UserResponseCodes.INVALID_LOGIN, "Invalid login")
        }

        val screenName = screenNameDbh.loadByUser(dbc, bu.user)


        return OkResponse(
            gson.toJson(
                RokLogin(
                    ctx.session.maxInactiveInterval,
                    SessionInfoUser(bu.user, screenName?.screen_name)
                )
            )
        )
    }
}

data class RokLogin(
    @SerializedName("session_ttl") val sessionTtl: Int,
    @SerializedName("session_info") val sessionInfo: SessionInfoUser
)