package com.bednar.dbexplorer.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
class TestcontainersUtil {

    private static final URI_START_INDEX = 5

    @Autowired
    DataSource dataSource

    int getConnectionPort() {
        def jdbcUrl = dataSource.getConnection().getMetaData().getURL()
        def url = jdbcUrl.substring(URI_START_INDEX) //removes 'jdbc:' from the connection string
        def uri = URI.create(url)

        return uri.getPort()
    }
}
