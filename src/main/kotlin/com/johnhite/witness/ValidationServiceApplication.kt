package com.johnhite.witness;

import com.google.inject.Guice
import com.google.inject.Injector
import com.johnhite.witness.resources.TestResource
import io.dropwizard.Application
import io.dropwizard.db.ManagedDataSource
import io.dropwizard.db.ManagedPooledDataSource
import io.dropwizard.jdbi3.JdbiFactory
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment

class ValidationServiceApplication : Application<ValidationServiceConfiguration>() {

    override fun getName() : String {
        return "Validation Service"
    }

    override fun initialize(bootstrap : Bootstrap<ValidationServiceConfiguration>) {

    }

    override fun run(configuration : ValidationServiceConfiguration, environment : Environment) {
        val injector = Guice.createInjector(ValidationServiceModule())

        environment.jersey().register(TestResource())

        val snowflake = configuration.snowflake.build(environment.metrics(), "snowflake")
        val mysql = configuration.mysql.build(environment.metrics(), "mysql")

        println("Snowflake")
        runQuery("select * from etl.shipment limit 10;", snowflake)
        println("MySQL")
        runQuery("select * from shipment limit 10;", mysql)
    }

    fun runQuery(query: String, ds: ManagedDataSource) {
        val connection = ds.connection
        val st = connection.prepareStatement(query)
        val rs = st.executeQuery()
        val cols = rs.metaData.columnCount
        var header = StringBuilder()
        for (c in 1..cols) {
            header.append(rs.metaData.getColumnName(c))
            header.append('\t')
        }
        println(header.toString())

        while (rs.next()) {
            var row = StringBuilder()
            for (c in 1..cols) {
                row.append(rs.getString(c))
                row.append('\t')
            }
            println(row.toString())
        }
        connection.close()
    }
}

fun main(args : Array<String>) {
    ValidationServiceApplication().run(*args)
}