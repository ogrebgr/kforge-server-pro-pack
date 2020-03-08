package org.example.kforgepro.modules.user.endpoints

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.handler.ForgeDbEndpoint
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.response.forge.MissingParametersResponse
import com.bolyartech.forge.server.response.forge.OkResponse
import com.bolyartech.forge.server.route.RequestContext
import com.google.gson.Gson
import org.example.kforgepro.modules.user.SessionInfoUser
import org.example.kforgepro.modules.user.data.NewUserResult
import org.example.kforgepro.modules.user.data.NewUserResultOK
import org.example.kforgepro.modules.user.data.User
import org.example.kforgepro.modules.user.data.UserDbOps
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
                return OkResponse(
                    gson.toJson(
                        RokLogin(
                            ctx.session.maxInactiveInterval,
                            SessionInfoUser(rez.user.id, null)
                        )
                    )
                )
            }
        }
    }
}