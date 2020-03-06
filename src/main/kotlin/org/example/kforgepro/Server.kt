package org.example.kforgepro

import com.bolyartech.forge.server.db.DbConfiguration
import com.bolyartech.forge.server.handler.RouteHandler
import com.bolyartech.forge.server.jetty.ForgeJetty
import com.bolyartech.forge.server.jetty.ForgeJettyConfiguration
import com.bolyartech.forge.server.module.HttpModule
import com.bolyartech.totoproverka3.server.main.MainModule
import com.mchange.v2.c3p0.ComboPooledDataSource
import org.eclipse.jetty.server.session.DatabaseAdaptor
import org.eclipse.jetty.server.session.JDBCSessionDataStore
import org.eclipse.jetty.server.session.JDBCSessionDataStoreFactory
import org.example.kforgepro.dagger.InternalServerErrorHandler
import org.example.kforgepro.dagger.NotFoundHandler
import org.example.kforgepro.modules.admin.AdminModule
import java.util.*
import javax.inject.Inject
import javax.servlet.http.HttpServlet
import javax.sql.DataSource

class Server @Inject constructor(
    private val mainModule: MainModule,
    private val adminModule: AdminModule,

    @NotFoundHandler private val notFoundHandler: RouteHandler,
    @InternalServerErrorHandler private val internalServerError: RouteHandler
) {
    private lateinit var forgeJetty: ForgeJetty

    fun start(jettyConfig: ForgeJettyConfiguration, dbConfig: DbConfiguration) {
        val dba: DatabaseAdaptor = DatabaseAdaptor()
        dba.datasource = createDataSource(dbConfig)
        val f: JDBCSessionDataStoreFactory = JDBCSessionDataStoreFactory()
        f.setDatabaseAdaptor(dba)
        f.setSessionTableSchema(JDBCSessionDataStore.SessionTableSchema())

        forgeJetty = ForgeJetty(jettyConfig, createMainServlet(), f)
        forgeJetty.start()
    }

    private fun createDataSource(conf: DbConfiguration): DataSource {
        val comboPooledDataSource = ComboPooledDataSource()
        comboPooledDataSource.jdbcUrl = conf.dbDsn
        comboPooledDataSource.user = conf.dbUsername
        comboPooledDataSource.password = conf.dbPassword
        comboPooledDataSource.maxStatements = conf.cacheMaxStatements
        comboPooledDataSource.initialPoolSize = conf.initialPoolSize
        comboPooledDataSource.minPoolSize = conf.minPoolSize
        comboPooledDataSource.maxPoolSize = conf.maxPoolSize
        comboPooledDataSource.idleConnectionTestPeriod = conf.idleConnectionTestPeriod
        comboPooledDataSource.isTestConnectionOnCheckin = conf.testConnectionOnCheckIn
        comboPooledDataSource.isTestConnectionOnCheckout = conf.testConnectionOnCheckout
        comboPooledDataSource.connectionCustomizerClassName = "com.bolyartech.forge.server.db.C3p0ConnectionCustomizer"

        return comboPooledDataSource
    }

    private fun createMainServlet(): HttpServlet {
        val modules = ArrayList<HttpModule>()
        modules.add(mainModule)
        modules.add(adminModule)

        return MainServlet(modules, notFoundHandler, internalServerError)
    }
}
