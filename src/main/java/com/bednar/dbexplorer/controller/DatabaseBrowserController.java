package com.bednar.dbexplorer.controller;

import com.bednar.dbexplorer.dto.DatabaseDetailsDTO;
import com.bednar.dbexplorer.service.DatabaseDetailsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/database/browse/")
public class DatabaseBrowserController {

    private final DatabaseDetailsService databaseDetailsService;

    @ApiOperation(value = "Lists schemas for the given database")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/{databaseId}/schemas")
    public List<String> listSchemas(@PathVariable("databaseId") final long databaseId) {
        log.debug("action=list_schemas database_id={} status=start", databaseId);
        try {
            final List<String> schemas = databaseDetailsService.getSchemas(databaseId);

            log.debug("action=list_schemas database_id={} status=ok", databaseId);
            return schemas;
        } catch (Exception e) {
            log.error("action=list_schemas database_id={} status=error", databaseId, e);
            throw e;
        }
    }

    @ApiOperation(value = "Lists tables for the given database schemas")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/{databaseId}/{schema}/tables")
    public List<String> listTables(@PathVariable("databaseId") final long databaseId,
                                   @PathVariable("schema") final String schema) {
        log.debug("action=list_tables database_id={} schema={} status=start", databaseId, schema);
        try {
            final List<String> tables = databaseDetailsService.getTables(databaseId, schema);

            log.debug("action=list_tables database_id={} schema={} status=ok", databaseId, schema);
            return tables;
        } catch (Exception e) {
            log.error("action=list_tables database_id={} schema={} status=error", databaseId, schema, e);
            throw e;
        }
    }

    @ApiOperation(value = "Lists table details for the given database table")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/{databaseId}/{schema}/{tableName}/details")
    public DatabaseDetailsDTO getTableDetails(@PathVariable("databaseId") final long databaseId,
                                              @PathVariable("schema") final String schema,
                                              @PathVariable("tableName") final String tableName) {
        log.debug("action=get_table_details database_id={} schema={} table_name={} status=start", databaseId, schema, tableName);
        try {
            final DatabaseDetailsDTO databaseDetails = databaseDetailsService.getTableDetails(databaseId, schema, tableName);

            log.debug("action=get_table_details database_id={} schema={} table_name={} status=ok", databaseId, schema, tableName);
            return databaseDetails;
        } catch (Exception e) {
            log.error("action=get_table_details database_id={} schema={} table_name={} status=error", databaseId, schema, tableName, e);
            throw e;
        }
    }

    @ApiOperation(value = "Preview of the table data for given database table",
            notes = "First 100 records are retrieved")
    @ApiResponse(examples = @Example(value = @ExampleProperty(value = "{ [columnName: columnData] }")), code = 200, message = "OK")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/{databaseId}/{schema}/{tableName}/data")
    public List<Map<String, Object>> getTableData(@PathVariable("databaseId") final long databaseId,
                                                  @PathVariable("schema") final String schema,
                                                  @PathVariable("tableName") final String tableName) {
        log.debug("action=get_table_data database_id={} schema={} table_name={} status=start", databaseId, schema, tableName);
        try {
            final List<Map<String, Object>> tableData = databaseDetailsService.getTableData(databaseId, schema, tableName);

            log.debug("action=get_table_data database_id={} schema={} table_name={} status=ok", databaseId, schema, tableName);
            return tableData;
        } catch (Exception e) {
            log.error("action=get_table_data database_id={} schema={} table_name={} status=error", databaseId, schema, tableName, e);
            throw e;
        }
    }
}
