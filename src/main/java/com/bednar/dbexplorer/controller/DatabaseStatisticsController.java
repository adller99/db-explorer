package com.bednar.dbexplorer.controller;


import com.bednar.dbexplorer.dto.TableStatisticsDTO;
import com.bednar.dbexplorer.service.DatabaseStatisticsService;
import com.bednar.dbexplorer.service.StatisticsOperation;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/database/statistics")
public class DatabaseStatisticsController {

    private final DatabaseStatisticsService databaseStatisticsService;

    @ApiOperation(value = "Computes basic column statistics", notes = "Supported operations: [min, max, average, median]")
    @GetMapping("/column/{databaseId}/{schema}/{tableName}/{columnName}/{operation}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Object getColumnStatistics(@PathVariable("databaseId") final long databaseId,
                              @PathVariable("schema") final String schema,
                              @PathVariable("tableName") final String tableName,
                              @PathVariable("columnName") final String columnName,
                              @PathVariable("operation") String operation) {
        log.debug("action=get_column_statistics database_id={}, schema={}, table_name={}, column_name={}, operation={} status=start",
                databaseId, schema, tableName, columnName, operation);
        try {
            final StatisticsOperation statisticsOperation = StatisticsOperation.valueOf(operation.toUpperCase(Locale.ROOT));
            final Object result = databaseStatisticsService.getColumnStatistics(databaseId, schema, tableName,
                    columnName, statisticsOperation);

            log.debug("action=get_column_statistics database_id={}, schema={}, table_name={}, column_name={}, operation={} status=ok",
                    databaseId, schema, tableName, columnName, operation);
            return result;
        } catch (Exception e) {
            log.error("action=get_column_statistics database_id={}, schema={}, table_name={}, column_name={}, operation={} status=error",
                    databaseId, schema, tableName, columnName, operation, e);
            throw e;
        }
    }

    @GetMapping("/table/{databaseId}/{schema}/{tableName}/")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public TableStatisticsDTO getTableStatistics(@PathVariable("databaseId") final long databaseId,
                                                @PathVariable("schema") final String schema,
                                                @PathVariable("tableName") final String tableName) {
        log.debug("action=get_table_statistics database_id={}, schema={}, table_name={} status=start", databaseId, schema, tableName);
        try {
            final TableStatisticsDTO result = databaseStatisticsService.getTableStatistics(databaseId, schema, tableName);

            log.debug("action=get_column_statistics database_id={}, schema={}, table_name={} status=ok", databaseId, schema, tableName);
            return result;
        } catch (Exception e) {
            log.error("action=get_column_statistics database_id={}, schema={}, table_name={} status=error", databaseId, schema, tableName, e);
            throw e;
        }
    }
}
