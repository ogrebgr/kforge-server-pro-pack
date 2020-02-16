package org.example.kforgepro.module.admin

import com.bolyartech.forge.server.HttpMethod
import com.bolyartech.forge.server.module.HttpModule
import com.bolyartech.forge.server.route.Route
import com.bolyartech.forge.server.route.RouteImpl
import org.example.kforgepro.module.admin.endpoints.CreateAdminUserEp
import java.util.*
import javax.inject.Inject

class AdminModule @Inject constructor(private val createAdminUserEp: CreateAdminUserEp) : HttpModule {
    private val MODULE_SYSTEM_NAME = "admin"
    private val MODULE_VERSION_CODE = 1
    private val MODULE_VERSION_NAME = "1.0.0"
    private val PATH_PREFIX = "/api1.0/admin"

    override fun createRoutes(): MutableList<Route> {
        val ret = ArrayList<Route>()
        ret.add(RouteImpl(HttpMethod.POST, "$PATH_PREFIX/admin_user_create", createAdminUserEp))
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