package org.example.kforgepro.dagger

import com.bolyartech.forge.server.handler.RouteHandler
import com.bolyartech.totoproverka3.server.main.NotFoundResponse
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import org.example.kforge.misc.InternalServerErrorResponse
import org.example.kforge.misc.TimeProvider
import org.example.kforge.misc.TimeProviderImpl
import javax.inject.Qualifier


@Module
class ServerModule(private val staticFilesDir: String) {
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @StaticFilesDir
    fun provideStaticFilesDir(): String {
        return staticFilesDir
    }

    @Provides
    @NotFoundHandler
    fun provideNotFoundHandler(): RouteHandler {
        return RouteHandler { NotFoundResponse() }
    }

    @Provides
    @InternalServerErrorHandler
    fun provideInternalServerErrorHandler(): RouteHandler {
        return RouteHandler { InternalServerErrorResponse() }
    }
}

@Module
abstract class ServerModuleBind {
    @Binds
    abstract fun provideTimeProvider(impl: TimeProviderImpl): TimeProvider
}


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class StaticFilesDir

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class NotFoundHandler

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class InternalServerErrorHandler