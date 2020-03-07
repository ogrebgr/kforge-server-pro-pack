package org.example.kforgepro.dagger

import com.bolyartech.forge.server.db.DbConfiguration
import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.db.DbUtils
import dagger.Binds
import dagger.Module
import dagger.Provides
import org.example.kforgepro.modules.admin.data.*
import org.example.kforgepro.modules.user.data.*
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
class DbDaggerModule(private val dbConfig: DbConfiguration) {

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

    @Provides
    @UserBlowfishAuto
    internal fun provideUserBlowfishDbh(): UserBlowfishDbh {
        return UserBlowfishDbhImpl("user_blowfish")
    }

    @Provides
    @UserBlowfishFinal
    internal fun provideUserBlowfish2Dbh(): UserBlowfishDbh {
        return UserBlowfishDbhImpl("user_blowfish2")
    }
}

@Module
abstract class DbBinds {
    @Binds
    internal abstract fun provideAdminUserDbf(impl: AdminUserDbhImpl): AdminUserDbh

    @Binds
    internal abstract fun provideAdminUserScramDbh(impl: AdminUserScramDbhImpl): AdminUserScramDbh

    @Binds
    internal abstract fun provideAdminUserExportedViewDbh(impl: AdminUserExportedViewDbhImpl): AdminUserExportedViewDbh

    @Binds
    internal abstract fun provideUserDbh(impl: UserDbhImpl): UserDbh

    @Binds
    internal abstract fun provideUserScreenNameDbh(impl: UserScreenNameDbhImpl): UserScreenNameDbh

    @Binds
    internal abstract fun provideUserDbOps(impl: UserDbOpsImpl): UserDbOps
}


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AdminScramDbh

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class UserScramDbh

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class UserBlowfishAuto

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class UserBlowfishFinal