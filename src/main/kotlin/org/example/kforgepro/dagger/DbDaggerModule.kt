package org.example.kforge.dagger

import com.bolyartech.forge.server.config.ForgeConfigurationException
import com.bolyartech.forge.server.db.DbConfiguration
import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.db.DbUtils
import com.bolyartech.forge.server.db.FileDbConfigurationLoader
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbDaggerModule(configDir: String) {
    val dbConfig: DbConfiguration

    init {
        val dbConfigurationLoader = FileDbConfigurationLoader(configDir)
        try {
            dbConfig = dbConfigurationLoader.load()
        } catch (e: ForgeConfigurationException) {
            throw IllegalStateException(e)
        }
    }

    @Provides
    @Singleton
    internal fun provideDbPool(): DbPool {
        return createDbPool()
    }

    private fun createDbPool(): DbPool {
        return DbUtils.createC3P0DbPool(dbConfig)
    }
}