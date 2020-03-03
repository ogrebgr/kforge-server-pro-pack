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

class AdminStoreSuperAdminEp @Inject constructor(dbPool: DbPool, private val adminUserDbh: AdminUserDbh) :
    AdminDbEndpoint(dbPool) {
    private val ERROR_NOT_UPDATED = -100
    private val PARAM_USER_ID = "user_id"
    private val PARAM_SUPER_ADMIN = "super_admin"

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

        val superAdmin = ctx.getFromPost(PARAM_SUPER_ADMIN) == "1"

        val rez = adminUserDbh.changeIsSuperAdmin(dbc, userId, superAdmin)

        return if (rez) {
            OkResponse.getInstance()
        } else {
            ForgeResponse(ERROR_NOT_UPDATED)
        }
    }
}