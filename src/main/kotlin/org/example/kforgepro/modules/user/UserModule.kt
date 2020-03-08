package org.example.kforgepro.modules.user

import com.bolyartech.forge.server.HttpMethod
import com.bolyartech.forge.server.module.HttpModule
import com.bolyartech.forge.server.route.Route
import com.bolyartech.forge.server.route.RouteImpl
import org.example.kforgepro.modules.user.endpoints.*
import java.util.*
import javax.inject.Inject

class UserModule @Inject constructor(
    private val userLoginBfEp: UserLoginBfEp,
    private val userRegisterBfEp: UserRegisterBfEp,
    private val userAutoRegisterBfEp: UserAutoRegisterBfEp,
    private val setScreenNameEp: SetScreenNameEp,
    private val userRegisterPostAutoBfEp: UserRegisterPostAutoBfEp,
    private val userLogoutEp: UserLogoutEp
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
        ret.add(RouteImpl(HttpMethod.POST, "$PATH_PREFIX/screen_name", setScreenNameEp))
        ret.add(RouteImpl(HttpMethod.POST, "$PATH_PREFIX/register_postauto", userRegisterPostAutoBfEp))
        ret.add(RouteImpl(HttpMethod.GET, "$PATH_PREFIX/logout", userLogoutEp))

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