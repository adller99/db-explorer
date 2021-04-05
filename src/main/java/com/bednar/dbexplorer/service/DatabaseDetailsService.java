package com.bednar.dbexplorer.service;

import com.bednar.dbexplorer.cache.JdbcTemplateService;
import com.bednar.dbexplorer.dto.ColumnDTO;
import com.bednar.dbexplorer.dto.DatabaseDetailsDTO;
import com.bednar.dbexplorer.dto.ForeignKeyDTO;
import com.bednar.dbexplorer.exception.ApiErrorException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.bednar.dbexplorer.service.DatabaseAttributeConstants.COLUMN_NAME;
import static com.bednar.dbexplorer.service.DatabaseAttributeConstants.COLUMN_SIZE;
import static com.bednar.dbexplorer.service.DatabaseAttributeConstants.FOREIGN_KEY_COLUMN_NAME;
import static com.bednar.dbexplorer.service.DatabaseAttributeConstants.FOREIGN_KEY_NAME;
import static com.bednar.dbexplorer.service.DatabaseAttributeConstants.IS_NULLABLE;
import static com.bednar.dbexplorer.service.DatabaseAttributeConstants.PRIMARY_KEY_COLUMN_NAME;
import static com.bednar.dbexplorer.service.DatabaseAttributeConstants.PRIMARY_KEY_NAME;
import static com.bednar.dbexplorer.service.DatabaseAttributeConstants.PRIMARY_KEY_TABLE_NAME_SCHEMA;
import static com.bednar.dbexplorer.service.DatabaseAttributeConstants.TABLE_NAME;
import static com.bednar.dbexplorer.service.DatabaseAttributeConstants.TABLE_SCHEMA;
import static com.bednar.dbexplorer.service.DatabaseAttributeConstants.TYPE_NAME;

@Slf4j
@RequiredArgsConstructor
@Service
public class DatabaseDetailsService {

    private static final String QUERY_TABLE_TEMPLATE = "SELECT * FROM %s.%s limit 100";

    private final JdbcTemplateService jdbcTemplateService;

    /**
     * Gets schemas for the given database
     */
    public List<String> getSchemas(final long databaseId) {
        try (final Connection connection = jdbcTemplateService.getConnection(databaseId);
             final ResultSet schemaResultSet = connection.getMetaData().getSchemas()) {

            final List<String> schemas = new ArrayList<>();
            while (schemaResultSet.next()) {
                schemas.add(schemaResultSet.getString(TABLE_SCHEMA));
            }

            return schemas;
        } catch (SQLException e) {
            log.error("action=get_schemas database_id={} status=error", databaseId, e);
            throw new ApiErrorException("An error occurred while getting the database schemas", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * Gets tables names for the given schema
     */
    public List<String> getTables(final long databaseId, final String schema) {
        try (final Connection connection = jdbcTemplateService.getConnection(databaseId);
             final ResultSet tableResultSet = connection.getMetaData().getTables(null, schema, null, null)) {

            final List<String> tables = new ArrayList<>();
            if (!tableResultSet.isBeforeFirst()) {
                return tables;
            }

            while (tableResultSet.next()) {
                tables.add(tableResultSet.getString(TABLE_NAME));
            }

            return tables;
        } catch (SQLException e) {
            log.error("action=get_tables database_id={} schema={} status=error", databaseId, schema, e);
            throw new ApiErrorException("An error occurred while getting the database tables",
                    HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * Gets preview (first 100 records) of the table data for the given schema and table
     * The data structure is {@link List} of {@link Map} where:
     * Key = column name
     * Value = column value
     */
    public List<Map<String, Object>> getTableData(final long databaseId, final String schema, final String tableName) {
        try {
            final JdbcTemplate jdbcTemplate = jdbcTemplateService.getJdbcTemplate(databaseId);
            final String sqlStatement = String.format(QUERY_TABLE_TEMPLATE, schema, tableName);

            final List<Map<String, Object>> tableDataResult = jdbcTemplate.query(sqlStatement, resultSet -> {
                final List<Map<String, Object>> tableData = new ArrayList<>();

                if (!resultSet.isBeforeFirst()) {
                    return tableData;
                }

                final int columnCount = resultSet.getMetaData().getColumnCount();
                while (resultSet.next()) {
                    final Map<String, Object> dataMap = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        final String columnName = resultSet.getMetaData().getColumnName(i);
                        final Object columnValue = resultSet.getObject(i);

                        dataMap.put(columnName, columnValue);
                    }
                    tableData.add(dataMap);
                }

                return tableData;
            });

            return tableDataResult;
        } catch (Exception e) {
            log.error("action=get_table_data database_id={} schema={} table_name={} status=error", databaseId, schema, tableName, e);
            throw new ApiErrorException("An error occurred while retrieving the table data",
                    HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * Gets table details for the given schema and table
     */
    public DatabaseDetailsDTO getTableDetails(final long databaseId, final String schema, final String tableName) {
        try (final Connection connection = jdbcTemplateService.getConnection(databaseId);
             final ResultSet columnsResultSet = connection.getMetaData().getColumns(null, schema, tableName, null)) {

            if (!columnsResultSet.isBeforeFirst()) {
                return null;
            }

            final Map<String, String> primaryKeyColumns = getPrimaryKeys(connection.getMetaData(), schema, tableName);
            final Map<String, ForeignKeyDTO> foreignKeyColumns = getForeignKeys(connection.getMetaData(), schema, tableName);

            final List<ColumnDTO> columns = new ArrayList<>();
            while (columnsResultSet.next()) {
                final ColumnDTO columnDTO = createColumn(columnsResultSet, primaryKeyColumns, foreignKeyColumns);
                columns.add(columnDTO);
            }

            return DatabaseDetailsDTO.builder()
                    .tableName(tableName)
                    .columns(columns)
                    .build();
        } catch (SQLException e) {
            log.error("action=get_table_details database_id={} schema={} table_name={} status=error", databaseId, schema, tableName, e);
            throw new ApiErrorException("An error occurred while getting the table details",
                    HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    private static ColumnDTO createColumn(final ResultSet columnsResultSet, final Map<String, String> primaryKeyColumns,
                                          final Map<String, ForeignKeyDTO> foreignKeyColumns) throws SQLException {
        final String columnName = columnsResultSet.getString(COLUMN_NAME);
        final ForeignKeyDTO foreignKey = foreignKeyColumns.get(columnName);
        final String primaryKeyName = primaryKeyColumns.get(columnName);
        final boolean isPrimaryKey = primaryKeyName != null && !primaryKeyName.isEmpty();
        final boolean isNullable = columnsResultSet.getString(IS_NULLABLE).equals("YES");

        final ColumnDTO.ColumnDTOBuilder columnDTOBuilder = ColumnDTO.builder()
                .columnName(columnName)
                .columnDataType(columnsResultSet.getString(TYPE_NAME))
                .columnSize(columnsResultSet.getInt(COLUMN_SIZE))
                .foreignKey(foreignKey)
                .primaryKeyName(primaryKeyName)
                .isPrimaryKey(isPrimaryKey)
                .isNullable(isNullable);

        return columnDTOBuilder.build();
    }

    /**
     * Returns map of found primary keys for the given schema and table
     * Key = column name
     * Value = primary key name
     */
    private static Map<String, String> getPrimaryKeys(final DatabaseMetaData metaData, final String schema,
                                                      final String tableName) throws SQLException {
        final Map<String, String> primaryKeys = new HashMap<>();

        final ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(null, schema, tableName);
        if (!primaryKeysResultSet.isBeforeFirst()) {
            return primaryKeys;
        }

        while (primaryKeysResultSet.next()) {
            final String columnName = primaryKeysResultSet.getString(COLUMN_NAME);
            final String primaryKeyName = primaryKeysResultSet.getString(PRIMARY_KEY_NAME);

            primaryKeys.put(columnName, primaryKeyName);
        }

        return primaryKeys;

    }

    /**
     * Returns map of found foreign keys for the given schema and table
     * Key = column name
     * Value = {@link ForeignKeyDTO} which holds some additional values
     */
    private static Map<String, ForeignKeyDTO> getForeignKeys(final DatabaseMetaData metaData, final String schema,
                                                             final String tableName) throws SQLException {
        final Map<String, ForeignKeyDTO> foreignKeys = new HashMap<>();

        final ResultSet foreignKeysResultSet = metaData.getImportedKeys(null, schema, tableName);
        if (!foreignKeysResultSet.isBeforeFirst()) {
            return foreignKeys;
        }

        while (foreignKeysResultSet.next()) {
            final ForeignKeyDTO foreignKey = ForeignKeyDTO.builder()
                    .referencedTableName(foreignKeysResultSet.getString(PRIMARY_KEY_TABLE_NAME_SCHEMA))
                    .referencedColumnName(foreignKeysResultSet.getString(PRIMARY_KEY_COLUMN_NAME))
                    .foreignKeyName(foreignKeysResultSet.getString(FOREIGN_KEY_NAME))
                    .build();

            final String columnName = foreignKeysResultSet.getString(FOREIGN_KEY_COLUMN_NAME);
            foreignKeys.put(columnName, foreignKey);
        }

        return foreignKeys;
    }
}
