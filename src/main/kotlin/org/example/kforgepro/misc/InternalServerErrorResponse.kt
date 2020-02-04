package org.example.kforge.misc

import com.bolyartech.forge.server.response.Response
import com.bolyartech.forge.server.response.ResponseException
import java.io.IOException
import java.io.PrintWriter
import javax.servlet.http.HttpServletResponse

class InternalServerErrorResponse : Response {
    @Throws(ResponseException::class)
    override fun toServletResponse(resp: HttpServletResponse) {
        resp.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        val pw: PrintWriter
        try {
            pw = resp.writer
            pw.print("500 Internal Server Error")
            pw.flush()
            pw.close()
        } catch (e: IOException) {
            throw ResponseException(e)
        }
    }
}