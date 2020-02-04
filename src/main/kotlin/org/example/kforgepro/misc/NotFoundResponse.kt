package com.bolyartech.totoproverka3.server.main

import com.bolyartech.forge.server.response.Response
import com.bolyartech.forge.server.response.ResponseException
import java.io.IOException
import java.io.PrintWriter
import javax.servlet.http.HttpServletResponse

class NotFoundResponse : Response {
    @Throws(ResponseException::class)
    override fun toServletResponse(resp: HttpServletResponse) {
        resp.status = HttpServletResponse.SC_NOT_FOUND
        val pw: PrintWriter
        try {
            pw = resp.writer
            pw.print("404 Not found")
            pw.flush()
            pw.close()
        } catch (e: IOException) {
            throw ResponseException(e)
        }
    }
}