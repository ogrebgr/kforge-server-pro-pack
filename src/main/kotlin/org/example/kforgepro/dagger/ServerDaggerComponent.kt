package org.example.kforge.dagger

import dagger.Component
import org.example.kforge.Server
import javax.inject.Singleton

@Singleton
@Component(modules = [ServerModule::class, ServerModuleBind::class, DbDaggerModule::class])
interface ServerDaggerComponent {
    fun provideServer(): Server
}