package org.example.kforgepro.modules.admin.endpoints

import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.response.forge.OkResponse
import com.bolyartech.forge.server.route.RequestContext
import org.example.kforgepro.modules.admin.AdminSessionVars
import org.example.kforgepro.modules.admin.data.AdminUser
import javax.inject.Inject

class AdminLogoutEp @Inject constructor() : AdminEndpoint() {
    override fun handle(ctx: RequestContext, user: AdminUser): ForgeResponse {
        ctx.session.removeVar(AdminSessionVars.VAR_USER)

        return OkResponse.getInstance()
    }
}
