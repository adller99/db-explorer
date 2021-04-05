package com.bednar.dbexplorer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ColumnDTO {

    private final String columnName;
    private final String columnDataType;
    private final int columnSize;
    private final boolean isNullable;
    private final boolean isPrimaryKey;
    private final String primaryKeyName;
    private final ForeignKeyDTO foreignKey;
}
