package org.example.kforgepro.modules.user.endpoints

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.response.forge.MissingParametersResponse
import com.bolyartech.forge.server.response.forge.OkResponse
import com.bolyartech.forge.server.route.RequestContext
import org.example.kforgepro.modules.user.UserResponseCodes
import org.example.kforgepro.modules.user.data.User
import org.example.kforgepro.modules.user.data.UserScreenName
import org.example.kforgepro.modules.user.data.UserScreenNameDbh
import java.sql.Connection
import javax.inject.Inject

class SetScreenNameEp @Inject constructor(
    dbPool: DbPool,
    private val userScreenNameDbh: UserScreenNameDbh
) : UserDbEndpoint(dbPool) {

    private val PARAM_SCREEN_NAME = "screen_name"

    override fun handle(ctx: RequestContext, dbc: Connection, user: User): ForgeResponse {
        val screenName = ctx.getFromPost(PARAM_SCREEN_NAME)

        if (screenName == null || screenName.isEmpty()) {
            return MissingParametersResponse.getInstance()
        }

        val existing = userScreenNameDbh.loadByUser(dbc, user.id)
        if (existing != null) {
            // already have screen name
            return ForgeResponse(UserResponseCodes.SCREEN_NAME_EXISTS, "Already have screen name")
        }

        if (!UserScreenName.isValid(screenName)) {
            return ForgeResponse(UserResponseCodes.INVALID_SCREEN_NAME, "Invalid screen name")
        }

        userScreenNameDbh.createNew(dbc, user.id, screenName)

        return OkResponse()
    }
}