package com.bednar.dbexplorer.service;

import com.bednar.dbexplorer.cache.JdbcTemplateService;
import com.bednar.dbexplorer.converter.DatabaseConverter;
import com.bednar.dbexplorer.domain.Database;
import com.bednar.dbexplorer.dto.DatabaseDTO;
import com.bednar.dbexplorer.dto.DatabaseRequestDTO;
import com.bednar.dbexplorer.repository.DatabaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DatabaseService {

    private final DatabaseRepository databaseRepository;
    private final DatabaseConverter databaseConverter;

    public Database findById(final long id) {
        return databaseRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("DatabaseDetails with id: %s was not found in the database", id)));
    }

    public Database saveDatabase(final DatabaseRequestDTO databaseRequestDTO) {
        final Database database = databaseConverter.fromRequestDTO(databaseRequestDTO);

        return databaseRepository.save(database);
    }

    @Transactional
    public Database updateDatabase(final long databaseId, final DatabaseRequestDTO databaseRequestDTO) {
        final Database database = findById(databaseId);

        database.setName(databaseRequestDTO.getName());
        database.setHostname(databaseRequestDTO.getHostname());
        database.setPort(databaseRequestDTO.getPort());
        database.setDatabaseName(databaseRequestDTO.getDatabaseName());
        database.setUsername(databaseRequestDTO.getUsername());

        if (!databaseRequestDTO.getPassword().isEmpty()) {
            database.setPassword(databaseRequestDTO.getPassword());
        }

        return databaseRepository.save(database);

    }
    public List<DatabaseDTO> getDatabases() {
        final List<Database> databaseList = databaseRepository.findAll();

        return databaseList.stream()
                .map(databaseConverter::toDTO)
                .collect(Collectors.toList());
    }
}
