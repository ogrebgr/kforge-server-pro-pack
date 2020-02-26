package org.example.kforgepro.modules.admin

import com.bolyartech.forge.server.response.forge.ForgeResponseCode

enum class AdminResponseCodes(private val _code: Int) : ForgeResponseCode {
    /**
     * Registration related codes
     */
    REGISTRATION_REFUSED(-7),
    USERNAME_EXISTS(-8),
    PASSWORD_TOO_SHORT(-9),
    INVALID_USERNAME(-10),
    INVALID_PASSWORD(-11),

    /**
     * Login related codes
     */
    INVALID_LOGIN(-12), // user + password does not match valid account
    NOT_LOGGED_IN(-13), // not logged in

    INSUFFICIENT_PRIVILEGES(-14);

    override fun getCode(): Int {
        return _code
    }
}