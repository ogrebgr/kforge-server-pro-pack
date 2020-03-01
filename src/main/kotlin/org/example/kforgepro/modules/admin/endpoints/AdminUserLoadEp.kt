package org.example.kforgepro.modules.admin.endpoints

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.response.forge.ErrorResponse
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.response.forge.MissingParametersResponse
import com.bolyartech.forge.server.response.forge.OkResponse
import com.bolyartech.forge.server.route.RequestContext
import com.google.gson.Gson
import org.example.kforgepro.modules.admin.data.AdminUser
import org.example.kforgepro.modules.admin.data.AdminUserDbh
import org.example.kforgepro.modules.admin.data.AdminUserExportedView
import org.example.kforgepro.modules.admin.data.AdminUserScramDbh
import java.sql.Connection
import javax.inject.Inject

class AdminUserLoadEp @Inject constructor(
    dbPool: DbPool,
    private val adminUserDbh: AdminUserDbh,
    private val adminUserScramDbh: AdminUserScramDbh
) : AdminDbEndpoint(dbPool) {
    private val PARAM_ID = "id"
    private val RESPONSE_CANNOT_FIND_USER = -100

    private val gson = Gson()

    override fun handle(ctx: RequestContext, dbc: Connection, user: AdminUser): ForgeResponse {
        val userIdStr = ctx.getFromGet(PARAM_ID)
        if (userIdStr == null || userIdStr.isEmpty()) {
            return MissingParametersResponse.getInstance()
        }

        val userId = try {
            userIdStr.toInt()
        } catch (e: NumberFormatException) {
            return ErrorResponse("")
        }

        val user = adminUserDbh.loadById(dbc, userId)
        if (user == null) {
            return ForgeResponse(RESPONSE_CANNOT_FIND_USER, "1")
        }

        val aus = adminUserScramDbh.loadByUserId(dbc, userId)
        if (aus == null) {
            return ForgeResponse(RESPONSE_CANNOT_FIND_USER, "2")
        }

        return OkResponse(
            gson.toJson(
                AdminUserExportedView(
                    user.id,
                    aus.username,
                    user.isDisabled,
                    user.isSuperAdmin,
                    user.name
                )
            )
        )
    }

}
