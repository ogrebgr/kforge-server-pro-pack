package org.example.kforgepro.module.admin.endpoints

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.handler.ForgeDbEndpoint
import com.bolyartech.forge.server.response.ResponseException
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.route.RequestContext
import org.example.kforgepro.module.admin.AdminResponseCodes
import org.example.kforgepro.module.admin.AdminSessionVars
import org.example.kforgepro.module.admin.data.AdminUser
import java.sql.Connection
import java.sql.SQLException

abstract class AdminDbEndpoint(dbPool: DbPool) : ForgeDbEndpoint(dbPool) {
    @Throws(ResponseException::class, SQLException::class)
    abstract fun handle(
        ctx: RequestContext,
        dbc: Connection,
        user: AdminUser
    ): ForgeResponse

    @Throws(ResponseException::class, SQLException::class)
    override fun handleForge(ctx: RequestContext, dbc: Connection): ForgeResponse {
        val session = ctx.session
        val user = session.getVar<AdminUser>(AdminSessionVars.VAR_USER)

        return if (user != null) {
            handle(ctx, dbc, user)
        } else {
            ForgeResponse(AdminResponseCodes.NOT_LOGGED_IN.code, "Not logged in")
        }
    }
}