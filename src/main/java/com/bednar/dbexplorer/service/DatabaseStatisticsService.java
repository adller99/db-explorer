package com.bednar.dbexplorer.service;

import com.bednar.dbexplorer.cache.JdbcTemplateService;
import com.bednar.dbexplorer.dto.TableStatisticsDTO;
import com.bednar.dbexplorer.exception.ApiErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DatabaseStatisticsService {

    private static final String MAX_QUERY_TEMPLATE = "select max(%s) from %s.%s";
    private static final String MIX_QUERY_TEMPLATE = "select min(%s) from %s.%s";
    private static final String AVERAGE_QUERY_TEMPLATE = "select avg(%s) from %s.%s";
    private static final String MEDIAN_QUERY_TEMPLATE = "select PERCENTILE_CONT(0.5) within group (order by %s) from %s.%s;";
    private static final String TABLE_STATISTICS_QUERY_TEMPLATE = "SELECT (SELECT COUNT(*) " +
            "         FROM   information_schema.columns " +
            "         WHERE  table_schema = '%s' AND table_name = '%s') AS columns_count, " +
            "         (SELECT COUNT(*) as number_of_records " +
            "         FROM   %s.%s) AS records_count;";

    private static final int COLUMN_STATS_DATA_INDEX = 1;
    private static final int TABLE_STATS_ATTRIBUTES_COUNT_INDEX = 1;
    private static final int TABLE_STATS_ROWS_COUNT_INDEX = 2;

    private final JdbcTemplateService jdbcTemplateService;

    public Object getColumnStatistics(final long databaseId, final String schema, final String tableName,
                                      final String columnName, final StatisticsOperation statisticsOperation) {
        try {
            final JdbcTemplate jdbcTemplate = jdbcTemplateService.getJdbcTemplate(databaseId);
            final String sqlStatement = getSqlQuery(schema, tableName, columnName, statisticsOperation);

            final Object result = jdbcTemplate.query(sqlStatement, resultSet -> {
                if (!resultSet.isBeforeFirst()) {
                    return null;
                }

                resultSet.next(); //we shouldn't be getting more than one row;
                return resultSet.getObject(COLUMN_STATS_DATA_INDEX);
            });

            return result;
        } catch (Exception e) {
            throw new ApiErrorException("An error occurred while generating the column statistics",
                    HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    public TableStatisticsDTO getTableStatistics(final long databaseId, final String schema, final String tableName) {
        try {
            final JdbcTemplate jdbcTemplate = jdbcTemplateService.getJdbcTemplate(databaseId);
            final String sqlStatement = String.format(TABLE_STATISTICS_QUERY_TEMPLATE, schema, tableName, schema, tableName);

            final TableStatisticsDTO result = jdbcTemplate.query(sqlStatement, resultSet -> {
                if (!resultSet.isBeforeFirst()) {
                    return null;
                }

                resultSet.next(); //we shouldn't be getting more than one row;
                return TableStatisticsDTO.builder()
                        .attributesCount(resultSet.getLong(TABLE_STATS_ATTRIBUTES_COUNT_INDEX))
                        .recordsCount(resultSet.getLong(TABLE_STATS_ROWS_COUNT_INDEX))
                        .build();
            });

            return result;
        } catch (Exception e) {
            throw new ApiErrorException("An error occurred while generating the table statistics",
                    HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    private static String getSqlQuery(final String schema, final String tableName,
                                      final String columnName, final StatisticsOperation statisticsOperation) {
        final String queryTemplate;
        switch (statisticsOperation) {
            case MAX:
                queryTemplate = MAX_QUERY_TEMPLATE;
                break;
            case MIN:
                queryTemplate = MIX_QUERY_TEMPLATE;
                break;
            case AVERAGE:
                queryTemplate = AVERAGE_QUERY_TEMPLATE;
                break;
            case MEDIAN:
                queryTemplate = MEDIAN_QUERY_TEMPLATE;
                break;
            default:
                throw new UnsupportedOperationException("The requested SQL operation is not supported");
        }

        return String.format(queryTemplate, columnName, schema, tableName);
    }
}
