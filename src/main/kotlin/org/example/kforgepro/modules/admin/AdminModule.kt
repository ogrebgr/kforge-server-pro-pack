package org.example.kforgepro.modules.admin

import com.bolyartech.forge.server.HttpMethod
import com.bolyartech.forge.server.module.HttpModule
import com.bolyartech.forge.server.route.Route
import com.bolyartech.forge.server.route.RouteImpl
import org.example.kforgepro.modules.admin.endpoints.AdminUserLoadEp
import org.example.kforgepro.modules.admin.endpoints.AdminUsersListEp
import org.example.kforgepro.modules.admin.endpoints.CreateAdminUserEp
import org.example.kforgepro.modules.admin.endpoints.LoginEp
import java.util.*
import javax.inject.Inject

class AdminModule @Inject constructor(
    private val createAdminUserEp: CreateAdminUserEp,
    private val loginEp: LoginEp,
    private val adminUsersListEp: AdminUsersListEp,
    private val adminUserLoadEp: AdminUserLoadEp
) : HttpModule {
    private val MODULE_SYSTEM_NAME = "admin"
    private val MODULE_VERSION_CODE = 1
    private val MODULE_VERSION_NAME = "1.0.0"
    private val PATH_PREFIX = "/api1.0/admin"

    override fun createRoutes(): MutableList<Route> {
        val ret = ArrayList<Route>()
        ret.add(RouteImpl(HttpMethod.POST, "$PATH_PREFIX/admin_user_create", createAdminUserEp))
        ret.add(RouteImpl(HttpMethod.POST, "$PATH_PREFIX/login", loginEp))
        ret.add(RouteImpl(HttpMethod.GET, "$PATH_PREFIX/user_list", adminUsersListEp))
        ret.add(RouteImpl(HttpMethod.GET, "$PATH_PREFIX/user", adminUserLoadEp))
        return ret
    }

    override fun getSystemName(): String {
        return MODULE_SYSTEM_NAME
    }


    override fun getShortDescription(): String {
        return ""
    }


    override fun getVersionCode(): Int {
        return MODULE_VERSION_CODE
    }


    override fun getVersionName(): String {
        return MODULE_VERSION_NAME
    }

}