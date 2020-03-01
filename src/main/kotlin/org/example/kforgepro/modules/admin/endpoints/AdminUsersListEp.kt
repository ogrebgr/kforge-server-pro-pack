package org.example.kforgepro.modules.admin.endpoints

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.response.forge.BasicResponseCodes
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.route.RequestContext
import com.google.common.base.Strings
import com.google.gson.Gson
import org.example.kforgepro.modules.admin.data.AdminUser
import org.example.kforgepro.modules.admin.data.AdminUserExportedView
import org.example.kforgepro.modules.admin.data.AdminUserExportedViewDbh
import java.sql.Connection
import javax.inject.Inject

class AdminUsersListEp @Inject constructor(
    dbPool: DbPool,
    private val adminUserExportedViewDbh: AdminUserExportedViewDbh
) :
    AdminDbEndpoint(dbPool) {

    private val USERS_PAGE_SIZE = 100

    private val gson = Gson()

    override fun handle(ctx: RequestContext, dbc: Connection, user: AdminUser): ForgeResponse {
        val idGreaterThanRaw = ctx.getFromPost("id")
        var id: Long = 0
        if (!Strings.isNullOrEmpty(idGreaterThanRaw)) {
            id = try {
                idGreaterThanRaw.toLong()
            } catch (e: NumberFormatException) {
                return ForgeResponse(BasicResponseCodes.Errors.INVALID_PARAMETER_VALUE, "Invalid id: $id")
            }
        }

        val users: List<AdminUserExportedView> = adminUserExportedViewDbh.list(
            dbc,
            id,
            USERS_PAGE_SIZE
        )

        return ForgeResponse(BasicResponseCodes.Oks.OK, gson.toJson(users))
    }
}