package org.example.kforgepro.modules.user.endpoints

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.handler.ForgeDbEndpoint
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.response.forge.MissingParametersResponse
import com.bolyartech.forge.server.response.forge.OkResponse
import com.bolyartech.forge.server.route.RequestContext
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.eclipse.jetty.util.security.Password
import org.example.kforgepro.modules.user.SessionInfoUser
import org.example.kforgepro.modules.user.UserSessionVars
import org.example.kforgepro.modules.user.data.*
import java.io.Serializable
import java.security.SecureRandom
import java.sql.Connection
import java.util.*
import javax.inject.Inject

class UserAutoRegisterBfEp @Inject constructor(
    dbPool: DbPool,
    private val userDbOps: UserDbOps
) : ForgeDbEndpoint(dbPool) {

    private val PARAM_INSTANCE_ID = "instance"

    private val gson = Gson()

    override fun handleForge(ctx: RequestContext, dbc: Connection): ForgeResponse {
        // TODO do device attestation first to check if real device OR do "burn first"

        if (ctx.getFromPost(PARAM_INSTANCE_ID) == null) {
            return MissingParametersResponse.getInstance()
        }

        val password = UUID.randomUUID().toString()
        var rez: NewUserResult
        while (true) {
            // adding "g" as a prefix in order to make the username valid when UUID starts with number
            val username = "g" + UUID.randomUUID().toString().replace("-", "")
            rez = userDbOps.createNewAuto(dbc, username, password)
            if (rez is NewUserResultOK) {
                val session = ctx.session
                session.setVar<Serializable>(UserSessionVars.VAR_USER, rez.user)
                session.setVar<Serializable>(UserSessionVars.VAR_LOGIN_TYPE, LoginType.NATIVE)

                return OkResponse(
                    gson.toJson(
                        RokAutoRegister(
                            username,
                            password,
                            ctx.session.maxInactiveInterval,
                            SessionInfoUser(rez.user.id, null)
                        )
                    )
                )
            }
        }
    }

    data class RokAutoRegister(
        val username: String,
        val password: String,
        @SerializedName("session_ttl") val sessionTtl: Int,
        @SerializedName("session_info") val sessionInfo: SessionInfoUser
    )
}
