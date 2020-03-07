package org.example.kforgepro.modules.user

import com.bolyartech.forge.server.HttpMethod
import com.bolyartech.forge.server.module.HttpModule
import com.bolyartech.forge.server.route.Route
import com.bolyartech.forge.server.route.RouteImpl
import org.example.kforgepro.modules.user.endpoints.UserAutoRegisterBfEp
import org.example.kforgepro.modules.user.endpoints.UserLoginBfEp
import org.example.kforgepro.modules.user.endpoints.UserRegisterBfEp
import java.util.*
import javax.inject.Inject

class UserModule @Inject constructor(
    private val userLoginBfEp: UserLoginBfEp,
    private val userRegisterBfEp: UserRegisterBfEp,
    private val userAutoRegisterBfEp: UserAutoRegisterBfEp
) : HttpModule {

    private val MODULE_SYSTEM_NAME = "user"
    private val MODULE_VERSION_CODE = 1
    private val MODULE_VERSION_NAME = "1.0.0"
    private val PATH_PREFIX = "/api1.0/user"

    override fun createRoutes(): MutableList<Route> {
        val ret = ArrayList<Route>()
        ret.add(RouteImpl(HttpMethod.POST, "$PATH_PREFIX/login", userLoginBfEp))
        ret.add(RouteImpl(HttpMethod.POST, "$PATH_PREFIX/register", userRegisterBfEp))
        ret.add(RouteImpl(HttpMethod.POST, "$PATH_PREFIX/autoreg", userAutoRegisterBfEp))


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