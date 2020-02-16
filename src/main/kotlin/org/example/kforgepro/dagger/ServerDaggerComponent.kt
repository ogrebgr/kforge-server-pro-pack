package org.example.kforgepro.dagger

import dagger.Component
import org.example.kforgepro.Server
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ServerModule::class,
        ServerModuleBind::class,
        DbDaggerModule::class,
        DbBinds::class]
)
interface ServerDaggerComponent {
    fun provideServer(): Server
}