package org.example.kforgepro.modules.admin.endpoints

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.misc.Params
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.response.forge.MissingParametersResponse
import com.bolyartech.forge.server.response.forge.OkResponse
import com.bolyartech.forge.server.route.RequestContext
import org.example.kforgepro.dagger.AdminScramDbh
import org.example.kforgepro.modules.admin.AdminResponseCodes
import org.example.kforgepro.modules.admin.data.*
import java.sql.Connection
import javax.inject.Inject

class CreateAdminUserEp @Inject constructor(
    dbPool: DbPool,
    private val adminUserDbh: AdminUserDbh,
    @AdminScramDbh private val adminScramDbh: ScramDbh,
    private val adminUserScramDbh: AdminUserScramDbh
) : AdminDbEndpoint(dbPool) {
    private val PARAM_USERNAME = "username"
    private val PARAM_PASSWORD = "password"
    private val PARAM_NAME = "name"
    private val PARAM_SUPER_ADMIN = "super_admin"

    override fun handle(ctx: RequestContext, dbc: Connection, user: AdminUser): ForgeResponse {
        return if (user.isSuperAdmin) {
            val username = ctx.getFromPost(PARAM_USERNAME)
            val password = ctx.getFromPost(PARAM_PASSWORD)
            val name = ctx.getFromPost(PARAM_NAME)
            val superAdminRaw = ctx.getFromPost(PARAM_SUPER_ADMIN)
            if (Params.areAllPresent(username, password, name)) {
                if (!Scram.isValidUsername(username)) {
                    return ForgeResponse(AdminResponseCodes.INVALID_USERNAME.code, "Invalid username")
                }

                if (!AdminUser.isValidPasswordLength(password.length)) {
                    return ForgeResponse(AdminResponseCodes.PASSWORD_TOO_SHORT.code, "Password too short")
                }

                val superAdmin = superAdminRaw != null && superAdminRaw == "1"

                val data = Scram.createPasswordData(password)

                val userNew = adminUserScramDbh.createNew(
                    dbc, adminUserDbh, adminScramDbh,
                    superAdmin, name, username, data
                )
                if (userNew != null) {
                    OkResponse()
                } else {
                    ForgeResponse(AdminResponseCodes.USERNAME_EXISTS.code, "Username already exist")
                }
            } else {
                MissingParametersResponse.getInstance()
            }
        } else {
            ForgeResponse(AdminResponseCodes.INSUFFICIENT_PRIVILEGES.code, "Insufficient privileges")
        }
    }
}