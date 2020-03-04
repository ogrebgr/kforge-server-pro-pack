package org.example.kforgepro.modules.admin.endpoints

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.response.forge.ErrorResponse
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.response.forge.MissingParametersResponse
import com.bolyartech.forge.server.response.forge.OkResponse
import com.bolyartech.forge.server.route.RequestContext
import org.example.kforgepro.dagger.AdminScramDbh
import org.example.kforgepro.modules.admin.AdminResponseCodes
import org.example.kforgepro.modules.admin.data.AdminUser
import org.example.kforgepro.modules.admin.data.Scram
import org.example.kforgepro.modules.admin.data.ScramDbh
import java.sql.Connection
import javax.inject.Inject

class AdminChangePasswordEp @Inject constructor(dbPool: DbPool, @AdminScramDbh private val scramDbh: ScramDbh) :
    AdminDbEndpoint(dbPool) {

    private val ERROR_CODE_PASSWORD_NOT_UPDATED = -100
    private val PARAM_USER_ID = "user_id"
    private val PARAM_PASSWORD = "password"


    override fun handle(ctx: RequestContext, dbc: Connection, user: AdminUser): ForgeResponse {
        val userIdStr = ctx.getFromPost(PARAM_USER_ID)
        if (userIdStr == null || userIdStr.isEmpty()) {
            return MissingParametersResponse.getInstance()
        }

        val password = ctx.getFromPost(PARAM_PASSWORD)
        if (password == null || password.isEmpty()) {
            return MissingParametersResponse.getInstance()
        }

        val userId = try {
            userIdStr.toInt()
        } catch (e: NumberFormatException) {
            return ErrorResponse("")
        }

        if (userId != user.id && !user.isSuperAdmin) {
            return ForgeResponse(AdminResponseCodes.INSUFFICIENT_PRIVILEGES.code, "Insufficient privileges")
        }

        val data = Scram.createPasswordData(password)

        return if (scramDbh.changePassword(dbc, userId, data)) {
            OkResponse.getInstance()
        } else {
            ForgeResponse(ERROR_CODE_PASSWORD_NOT_UPDATED)
        }
    }
}