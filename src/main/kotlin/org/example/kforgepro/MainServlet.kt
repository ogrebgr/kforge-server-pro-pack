package org.example.kforgepro

import com.bolyartech.forge.server.BaseServletDefaultImpl
import com.bolyartech.forge.server.handler.RouteHandler
import com.bolyartech.forge.server.module.HttpModule

class MainServlet(modules: List<HttpModule>, notFoundHandler: RouteHandler, internalServerErrorHandler: RouteHandler) :
    BaseServletDefaultImpl(modules, notFoundHandler, internalServerErrorHandler)