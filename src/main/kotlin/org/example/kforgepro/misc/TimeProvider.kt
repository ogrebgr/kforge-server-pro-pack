package org.example.kforge.misc

import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

interface TimeProvider {
    fun getVmTime(): Long
    fun getWallClockTime(): LocalDateTime
    fun getWallClockTimeMillis(): Long
}


class TimeProviderImpl @Inject constructor() : TimeProvider {
    override fun getVmTime(): Long {
        return System.nanoTime() / 1000000
    }

    override fun getWallClockTime(): LocalDateTime {
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(System.currentTimeMillis()),
            TimeZone.getDefault().toZoneId()
        )
    }

    override fun getWallClockTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}