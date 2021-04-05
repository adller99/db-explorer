package com.bednar.dbexplorer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForeignKeyDTO {

    private final String referencedTableName;
    private final String referencedColumnName;
    private final String foreignKeyName;
}
