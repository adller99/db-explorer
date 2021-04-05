package com.bednar.dbexplorer.converter;

import com.bednar.dbexplorer.domain.Database;
import com.bednar.dbexplorer.dto.DatabaseDTO;
import com.bednar.dbexplorer.dto.DatabaseRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConverter {

    public Database fromRequestDTO(final DatabaseRequestDTO databaseRequestDTO) {
        return Database.builder()
                .name(databaseRequestDTO.getName())
                .hostname(databaseRequestDTO.getHostname())
                .port(databaseRequestDTO.getPort())
                .databaseName(databaseRequestDTO.getDatabaseName())
                .username(databaseRequestDTO.getUsername())
                .password(databaseRequestDTO.getPassword())
                .build();
    }

    public DatabaseDTO toDTO(final Database database) {
        return DatabaseDTO.builder()
                .id(database.getId())
                .name(database.getName())
                .hostname(database.getHostname())
                .port(database.getPort())
                .databaseName(database.getDatabaseName())
                .username(database.getUsername())
                .build();
    }
}
