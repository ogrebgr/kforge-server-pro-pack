package org.example.kforge.modules.main.endpoints

import com.bolyartech.forge.server.handler.ResourceNotFoundException
import com.bolyartech.forge.server.handler.RouteHandler
import com.bolyartech.forge.server.response.HtmlResponse
import com.bolyartech.forge.server.response.Response
import com.bolyartech.forge.server.response.ResponseException
import com.bolyartech.forge.server.route.RequestContext

class RootWp : RouteHandler {
    @Throws(ResponseException::class, ResourceNotFoundException::class)
    override fun handle(ctx: RequestContext): Response {
        return HtmlResponse("KForge")
    }
}
