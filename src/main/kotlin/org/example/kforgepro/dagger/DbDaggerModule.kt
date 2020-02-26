package org.example.kforgepro.dagger

import com.bolyartech.forge.server.config.ForgeConfigurationException
import com.bolyartech.forge.server.db.DbConfiguration
import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.db.DbUtils
import com.bolyartech.forge.server.db.FileDbConfigurationLoader
import dagger.Binds
import dagger.Module
import dagger.Provides
import org.example.kforgepro.modules.admin.data.*
import javax.inject.Qualifier
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


    @Provides
    @AdminScramDbh
    internal fun provideAdminScramDbh(): ScramDbh {
        return ScramDbhImpl("admin_user_scram")
    }
}

@Module
abstract class DbBinds {
    @Binds
    internal abstract fun provideAdminUserDbf(impl: AdminUserDbhImpl): AdminUserDbh

    @Binds
    internal abstract fun provideAdminUserScramDbh(impl: AdminUserScramDbhImpl): AdminUserScramDbh
}


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AdminScramDbh

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class UserScramDbh