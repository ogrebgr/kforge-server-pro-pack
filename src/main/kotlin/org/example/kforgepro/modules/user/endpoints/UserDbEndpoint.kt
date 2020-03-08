package org.example.kforgepro.modules.user.endpoints

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.handler.ForgeDbEndpoint
import com.bolyartech.forge.server.response.ResponseException
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.route.RequestContext
import org.example.kforgepro.modules.admin.data.AdminUser
import org.example.kforgepro.modules.user.UserResponseCodes
import org.example.kforgepro.modules.user.UserSessionVars
import org.example.kforgepro.modules.user.data.User
import java.sql.Connection
import java.sql.SQLException

abstract class UserDbEndpoint(dbPool: DbPool) : ForgeDbEndpoint(dbPool) {
    @Throws(ResponseException::class, SQLException::class)
    abstract fun handle(
        ctx: RequestContext,
        dbc: Connection,
        user: User
    ): ForgeResponse

    @Throws(ResponseException::class, SQLException::class)
    override fun handleForge(ctx: RequestContext, dbc: Connection): ForgeResponse {
        val session = ctx.session
        val user = session.getVar<User>(
            UserSessionVars.VAR_USER
        )

        return if (user != null) {
            handle(ctx, dbc, user)
        } else {
            ForgeResponse(UserResponseCodes.NOT_LOGGED_IN.code, "Not logged in")
        }
    }
}