package org.example.kforge

import com.bolyartech.forge.server.handler.RouteHandler
import com.bolyartech.forge.server.jetty.ForgeJetty
import com.bolyartech.forge.server.jetty.ForgeJettyConfiguration
import com.bolyartech.forge.server.module.HttpModule
import com.bolyartech.totoproverka3.server.main.MainModule
import org.example.kforge.dagger.InternalServerErrorHandler
import org.example.kforge.dagger.NotFoundHandler
import java.util.*
import javax.inject.Inject
import javax.servlet.http.HttpServlet

class Server @Inject constructor(
    private val mainModule: MainModule,
    @NotFoundHandler private val notFoundHandler: RouteHandler,
    @InternalServerErrorHandler private val internalServerError: RouteHandler
) {
    private lateinit var forgeJetty: ForgeJetty

    fun start(jettyConfig: ForgeJettyConfiguration) {
        forgeJetty = ForgeJetty(jettyConfig, createMainServlet())
        forgeJetty.start()
    }

    private fun createMainServlet(): HttpServlet {
        val modules = ArrayList<HttpModule>()
        modules.add(mainModule)

        return MainServlet(modules, notFoundHandler, internalServerError)
    }
}
