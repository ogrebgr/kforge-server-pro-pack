package org.example.kforgepro

import com.bolyartech.forge.server.BaseServletDefaultImpl
import com.bolyartech.forge.server.handler.RouteHandler
import com.bolyartech.forge.server.module.HttpModule

class MainServlet(
    modules: List<HttpModule>, isPathInfoEnabled: Boolean,
    maxPathSegments: Int, notFoundHandler: RouteHandler, internalServerErrorHandler: RouteHandler
) :
    BaseServletDefaultImpl(modules, isPathInfoEnabled, maxPathSegments, notFoundHandler, internalServerErrorHandler)