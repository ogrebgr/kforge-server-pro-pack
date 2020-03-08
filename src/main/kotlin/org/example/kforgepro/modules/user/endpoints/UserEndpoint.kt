package org.example.kforgepro.modules.user.endpoints

import com.bolyartech.forge.server.handler.ForgeEndpoint
import com.bolyartech.forge.server.response.ResponseException
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.route.RequestContext
import org.example.kforgepro.modules.user.UserResponseCodes
import org.example.kforgepro.modules.user.UserSessionVars
import org.example.kforgepro.modules.user.data.User
import java.sql.SQLException

abstract class UserEndpoint : ForgeEndpoint() {
    @Throws(ResponseException::class, SQLException::class)
    abstract fun handle(
        ctx: RequestContext,
        user: User
    ): ForgeResponse

    @Throws(ResponseException::class, SQLException::class)
    override fun handleForge(ctx: RequestContext): ForgeResponse {
        val session = ctx.session
        val user = session.getVar<User>(
            UserSessionVars.VAR_USER
        )

        return if (user != null) {
            handle(ctx, user)
        } else {
            ForgeResponse(UserResponseCodes.NOT_LOGGED_IN.code, "Not logged in")
        }
    }
}