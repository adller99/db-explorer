package com.bednar.dbexplorer.cache;

import com.bednar.dbexplorer.domain.Database;
import com.bednar.dbexplorer.exception.ApiErrorException;
import com.bednar.dbexplorer.service.DatabaseService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.time.Duration;
import java.util.Objects;


@Slf4j
@Component
class JdbcTemplateCache {

    private static final String CONNECTION_URL_TEMPLATE = "jdbc:postgresql://%s:%s/%s";
    private final int maxConnectionPoolSize;

    private final DatabaseService databaseService;

    @Getter
    private final LoadingCache<Long, JdbcTemplate> jdbcTemplateLoadingCache;

    public JdbcTemplateCache(final DatabaseService databaseService,
                             @Value("${db-explorer.database-cache.expiry}") final Duration expiryAfterAccess,
                             @Value("${db-explorer.max-connection-pool}") final int maxConnectionPoolSize) {
        this.databaseService = databaseService;
        this.maxConnectionPoolSize = maxConnectionPoolSize;

        jdbcTemplateLoadingCache = CacheBuilder.newBuilder()
                .expireAfterAccess(expiryAfterAccess)
                .removalListener(this::onCacheRemoval)
                .build(new JdbcTemplateCacheLoader());
    }

    private void onCacheRemoval(final RemovalNotification<Long, JdbcTemplate> notification) {
        final long databaseId = notification.getKey();
        try {
            final JdbcTemplate jdbcTemplate = notification.getValue();
            final Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();

            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            log.error("action=remove_database_connection database_id={} status=error", databaseId, e);
        }
    }

    private class JdbcTemplateCacheLoader extends CacheLoader<Long, JdbcTemplate> {

        @Override
        public JdbcTemplate load(final Long databaseId) {
            try {
                final Database database = databaseService.findById(databaseId);

                final String connectionUrl = createConnectionUrl(database.getHostname(),
                        database.getPort(), database.getDatabaseName());

                final HikariConfig config = new HikariConfig();
                config.setJdbcUrl(connectionUrl);
                config.setPoolName("DatabaseConnectionPool_" + databaseId);
                config.setMaximumPoolSize(maxConnectionPoolSize);
                config.setPassword(database.getPassword());
                config.setUsername(database.getUsername());
                config.setDriverClassName("org.postgresql.Driver");

                final HikariDataSource dataSource = new HikariDataSource(config);

                return new JdbcTemplate(dataSource);
            } catch (Exception e) {
                log.error("action=load_database_connection database_id={} status=error", databaseId, e);
                throw new ApiErrorException("The database connection was not successful", HttpStatus.INTERNAL_SERVER_ERROR, e);
            }
        }
    }

    private static String createConnectionUrl(final String hostname, final int port, final String databaseName) {
        return String.format(CONNECTION_URL_TEMPLATE, hostname, port, databaseName);
    }
}
