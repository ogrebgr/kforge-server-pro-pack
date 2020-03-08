package org.example.kforgepro.modules.user.endpoints

import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.response.forge.OkResponse
import com.bolyartech.forge.server.route.RequestContext
import org.example.kforgepro.modules.user.UserSessionVars
import org.example.kforgepro.modules.user.data.User
import javax.inject.Inject

class UserLogoutEp @Inject constructor() : UserEndpoint() {
    override fun handle(ctx: RequestContext, user: User): ForgeResponse {
        ctx.session.removeVar(UserSessionVars.VAR_USER)
        ctx.session.removeVar(UserSessionVars.VAR_LOGIN_TYPE)

        return OkResponse.getInstance()
    }
}