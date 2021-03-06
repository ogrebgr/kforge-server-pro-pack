package org.example.kforgepro.modules.admin.endpoints

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.handler.ForgeDbEndpoint
import com.bolyartech.forge.server.misc.Params
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.response.forge.InvalidParameterValueResponse
import com.bolyartech.forge.server.response.forge.MissingParametersResponse
import com.bolyartech.forge.server.response.forge.OkResponse
import com.bolyartech.forge.server.route.RequestContext
import com.bolyartech.forge.server.session.Session
import com.bolyartech.scram_sasl.common.Base64
import com.bolyartech.scram_sasl.common.ScramException
import com.bolyartech.scram_sasl.server.ScramServerFunctionality
import com.bolyartech.scram_sasl.server.ScramServerFunctionalityImpl
import com.bolyartech.scram_sasl.server.UserData
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.example.kforgepro.dagger.AdminScramDbh
import org.example.kforgepro.modules.admin.AdminResponseCodes
import org.example.kforgepro.modules.admin.AdminSessionVars
import org.example.kforgepro.modules.admin.data.*
import java.sql.Connection
import java.sql.SQLException
import javax.inject.Inject


class AdminLoginEp @Inject constructor(
    dbPool: DbPool,
    private val gson: Gson,
    private val adminUserDbh: AdminUserDbh,
    @AdminScramDbh private val scramDbh: ScramDbh
) :
    ForgeDbEndpoint(dbPool) {

    private val PAUSE_AFTER_UNSUCCESSFUL_LOGIN_MILLIS = 500L

    private val PARAM_STEP = "step"
    private val PARAM_DATA = "data"

    override fun handleForge(ctx: RequestContext, dbc: Connection): ForgeResponse {
        val stepStr = ctx.getFromPost(PARAM_STEP)
        val data = ctx.getFromPost(PARAM_DATA)

        if (Params.areAllPresent(stepStr, data)) {
            return try {
                val session = ctx.session
                val step = stepStr.toInt()
                if (step == 1) {
                    handleStep1(dbc, session, data)
                } else if (step == 2) {
                    handleStep2(dbc, session, data)!!
                } else {
                    InvalidParameterValueResponse("invalid step")
                }
            } catch (e: NumberFormatException) {
                InvalidParameterValueResponse("step not integer")
            }
        } else {
            return MissingParametersResponse.getInstance()
        }
    }


    @Throws(SQLException::class)
    private fun handleStep2(
        dbc: Connection,
        session: Session,
        data: String
    ): ForgeResponse? {
        val scram = gson.fromJson(
            session.getVar<String>(AdminSessionVars.VAR_SCRAM_FUNC),
            ScramServerFunctionalityImpl::class.java
        )
        return if (scram != null) {
            if (scram.state == ScramServerFunctionality.State.PREPARED_FIRST) {
                try {
                    val finalMsg = scram.prepareFinalMessage(data)
                    if (finalMsg != null) {
                        val scramData: Scram = gson.fromJson(
                            session.getVar<String>(
                                AdminSessionVars.VAR_SCRAM_DATA
                            ), Scram::class.java
                        )
                        val user: AdminUser? = adminUserDbh.loadById(dbc, scramData.user)
                        if (user!!.isDisabled) {
                            ForgeResponse(AdminResponseCodes.INVALID_LOGIN, "User is disabled")
                        }
                        val si = SessionInfoAdmin(
                            user.id,
                            user.isSuperAdmin
                        )
                        session.setVar(AdminSessionVars.VAR_USER, user)
                        OkResponse(
                            gson.toJson(
                                RokLogin(
                                    session.maxInactiveInterval,
                                    si,
                                    finalMsg,
                                    user.isSuperAdmin
                                )
                            )
                        )
                    } else {
                        ForgeResponse(AdminResponseCodes.INVALID_LOGIN.code, "Invalid Login")
                    }
                } catch (e: ScramException) {
                    ForgeResponse(AdminResponseCodes.INVALID_LOGIN, "Invalid Login")
                }
            } else {
                session.removeVar(AdminSessionVars.VAR_SCRAM_FUNC)
                ForgeResponse(AdminResponseCodes.INVALID_LOGIN, "Invalid Login")
            }
        } else {
            ForgeResponse(AdminResponseCodes.INVALID_LOGIN, "Invalid Login")
        }
    }


    private fun handleStep1(
        dbc: Connection,
        session: Session,
        data: String
    ): ForgeResponse {
        session.removeVar(AdminSessionVars.VAR_SCRAM_FUNC)
        val scram: ScramServerFunctionality = ScramServerFunctionalityImpl(
            Scram.DEFAULT_DIGEST,
            Scram.DEFAULT_HMAC
        )
        val username = scram.handleClientFirstMessage(data)
        return if (username != null) {
            val scramData: Scram? = scramDbh.loadByUsername(dbc, username)
            if (scramData != null) {
                session.setVar(AdminSessionVars.VAR_SCRAM_DATA, gson.toJson(scramData))
                val ud = UserData(
                    Base64.encodeBytes(scramData.salt, Base64.DONT_BREAK_LINES),
                    scramData.iterations,
                    Base64.encodeBytes(scramData.serverKey, Base64.DONT_BREAK_LINES),
                    Base64.encodeBytes(scramData.storedKey, Base64.DONT_BREAK_LINES)
                )
                val first = scram.prepareFirstMessage(ud)
                session.setVar(AdminSessionVars.VAR_SCRAM_FUNC, gson.toJson(scram))
                OkResponse(first)
            } else {
                Thread.sleep(PAUSE_AFTER_UNSUCCESSFUL_LOGIN_MILLIS)
                ForgeResponse(AdminResponseCodes.INVALID_LOGIN, "Invalid Login")
            }
        } else {
            InvalidParameterValueResponse("Invalid data")
        }
    }


    data class RokLogin(
        @SerializedName("session_ttl") val sessionTtl: Int,
        @SerializedName("session_info") val sessionInfo: SessionInfoAdmin,
        @SerializedName("final_message") val finalMessage: String,
        @SerializedName("is_super_admin") val isSuperAdmin: Boolean
    )
}