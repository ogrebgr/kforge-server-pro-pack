package org.example.kforgepro.module.admin.endpoints

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.handler.ForgeDbEndpoint
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.response.forge.OkResponse
import com.bolyartech.forge.server.route.RequestContext
import org.example.kforgepro.module.admin.data.AdminUserDbh
import org.example.kforgepro.module.admin.data.ScramDbh
import java.sql.Connection


class LoginEp(dbPool: DbPool, private val adminUserDbh: AdminUserDbh, private val scramDbh: ScramDbh) :
    ForgeDbEndpoint(dbPool) {
    private val PARAM_STEP = "step"
    private val PARAM_DATA = "data"


    override fun handleForge(ctx: RequestContext, dbc: Connection): ForgeResponse {
        val stepStr = ctx.getFromPost(PARAM_STEP)
        val data = ctx.getFromPost(PARAM_DATA)



        return OkResponse()
    }
}