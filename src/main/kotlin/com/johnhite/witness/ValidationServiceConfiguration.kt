package com.johnhite.witness;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory
import org.hibernate.validator.constraints.*;
import javax.validation.constraints.*;

class ValidationServiceConfiguration : Configuration() {

    var snowflake: DataSourceFactory = DataSourceFactory()
    var mysql: DataSourceFactory = DataSourceFactory()

}
