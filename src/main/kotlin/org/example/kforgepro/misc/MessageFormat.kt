package org.example.kforge.misc

import org.slf4j.helpers.MessageFormatter

fun msgFormat(msg: String, vararg params: Any?): String {
    return if (params.size == 1) {
        MessageFormatter.format(msg, params[0]).message
    } else if (params.size > 1) {
        MessageFormatter.arrayFormat(msg, params).message
    } else {
        MessageFormatter.format(msg, params).message
    }
}