package org.example.kforgepro.modules.admin.endpoints

import com.bolyartech.forge.server.handler.ForgeEndpoint
import com.bolyartech.forge.server.response.ResponseException
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.route.RequestContext
import org.example.kforgepro.modules.admin.AdminResponseCodes
import org.example.kforgepro.modules.admin.AdminSessionVars
import org.example.kforgepro.modules.admin.data.AdminUser

abstract class AdminEndpoint : ForgeEndpoint() {
    @Throws(ResponseException::class)
    abstract fun handle(ctx: RequestContext, user: AdminUser): ForgeResponse

    @Throws(ResponseException::class)
    override fun handleForge(ctx: RequestContext): ForgeResponse {
        val session = ctx.session
        val user = session.getVar<AdminUser>(
            AdminSessionVars.VAR_USER
        )

        return if (user != null) {
            handle(ctx, user)
        } else {
            ForgeResponse(AdminResponseCodes.NOT_LOGGED_IN.code, "Not logged in")
        }
    }
}