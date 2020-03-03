package org.example.kforgepro.modules.admin.endpoints

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.response.forge.ErrorResponse
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.response.forge.MissingParametersResponse
import com.bolyartech.forge.server.response.forge.OkResponse
import com.bolyartech.forge.server.route.RequestContext
import org.example.kforgepro.modules.admin.AdminResponseCodes
import org.example.kforgepro.modules.admin.data.AdminUser
import org.example.kforgepro.modules.admin.data.AdminUserDbh
import java.sql.Connection
import javax.inject.Inject

class AdminStoreDisableEp @Inject constructor(dbPool: DbPool, private val adminUserDbh: AdminUserDbh) :
    AdminDbEndpoint(dbPool) {

    private val ERROR_NOT_UPDATED = -100
    private val PARAM_USER_ID = "user_id"
    private val PARAM_DISABLED = "disabled"

    override fun handle(ctx: RequestContext, dbc: Connection, user: AdminUser): ForgeResponse {
        if (!user.isSuperAdmin) {
            return ForgeResponse(AdminResponseCodes.INSUFFICIENT_PRIVILEGES.code, "Insufficient privileges")
        }

        val userIdStr = ctx.getFromPost(PARAM_USER_ID)
        if (userIdStr == null || userIdStr.isEmpty()) {
            return MissingParametersResponse.getInstance()
        }

        val userId = try {
            userIdStr.toInt()
        } catch (e: NumberFormatException) {
            return ErrorResponse("")
        }

        val disabled = ctx.getFromPost(PARAM_DISABLED) == "1"

        val rez = adminUserDbh.changeIsDisabled(dbc, userId, disabled)
        if (rez) {
            return OkResponse.getInstance()
        } else {
            return ForgeResponse(ERROR_NOT_UPDATED)
        }
    }
}