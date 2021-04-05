package com.bednar.dbexplorer.cache;

import com.bednar.dbexplorer.exception.ApiErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class JdbcTemplateService {

    private final JdbcTemplateCache jdbcTemplateCache;

    public Connection getConnection(final long databaseId) {
        try {
            final JdbcTemplate jdbcTemplate = jdbcTemplateCache.getJdbcTemplateLoadingCache().getUnchecked(databaseId);

            return Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
        } catch (Exception e) {
            log.error("action=get_connection database_id={} status=error", databaseId, e);
            throw new ApiErrorException("Database unavailable", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    public JdbcTemplate getJdbcTemplate(final long databaseId) {
        try {
            return jdbcTemplateCache.getJdbcTemplateLoadingCache().getUnchecked(databaseId);
        } catch (Exception e) {
            log.error("action=get_jdbc_template database_id={} status=error", databaseId, e);
            throw new ApiErrorException("Database unavailable", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    public void invalidate(final long databaseId) {
        jdbcTemplateCache.getJdbcTemplateLoadingCache().invalidate(databaseId);
    }
}
