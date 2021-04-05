package com.bednar.dbexplorer.controller

import com.bednar.dbexplorer.IntegrationTest
import com.bednar.dbexplorer.domain.Database
import com.bednar.dbexplorer.dto.DatabaseDTO
import com.bednar.dbexplorer.dto.DatabaseRequestDTO
import com.bednar.dbexplorer.repository.DatabaseRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@IntegrationTest
class DatabaseControllerSpec extends Specification {

    @Autowired
    DatabaseRepository databaseRepository

    @Autowired
    MockMvc mockMvc

    @Autowired
    ObjectMapper objectMapper

    def "save new database"() {
        given: "database request exists"
        def databaseRequest = DatabaseRequestDTO.builder()
                .name("main database")
                .hostname("10.50.15.62")
                .port(5558)
                .databaseName("master")
                .username("administrator")
                .password("veslo")
                .build()

        when: "when the save database endpoint is called"
        def response = mockMvc
                .perform(post("/database/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(databaseRequest))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andReturn().response

        then: "response header location contains database ID"
        def location = response.getHeader("location")
        def databaseId = location.replaceFirst("/database/", "") as Long

        and: "the entity exists in the database"
        def persistedDatabase = databaseRepository.findById(databaseId).get()

        and: "the persisted entity contains correct data"
        with(persistedDatabase) {
            name == databaseRequest.name
            hostname == databaseRequest.hostname
            port == databaseRequest.port
            databaseName == databaseRequest.databaseName
            username == databaseRequest.username
            password == databaseRequest.password
        }
    }

    def "list databases"() {
        given: "a database entity exists"
        def database = createDatabase()

        when: "the endpoint listing existing databases is called"
        def response = mockMvc.perform(get("/database"))
                .andReturn().response

        then: "the response holds correct response"
        List<DatabaseDTO> databaseDTOs = objectMapper.readValue(response.getContentAsString(),
                new TypeReference<ArrayList<DatabaseDTO>>() {})

        and: "it only contains one item"
        databaseDTOs.size() == 1

        and: "it contains correct data"
        with(databaseDTOs.get(0)) {
            name == database.name
            hostname == database.hostname
            port == database.port
            databaseName == database.databaseName
            username == database.username
        }
    }

    def "update the existing database"() {
        given: "a database entity exists"
        def database = createDatabase()

        and: "a database request DTO exists"
        def databaseRequest = DatabaseRequestDTO.builder()
                .name("secondary database")
                .hostname("10.99.55.32")
                .port(9652)
                .databaseName("not master")
                .username("user")
                .password("secret_password")
                .build()

        when:
        mockMvc.perform(put("/database/${database.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(databaseRequest))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent())
                .andReturn().response

        then:
        def persistedDatabase = databaseRepository.findById(database.id).get()

        with(persistedDatabase) {
            name == databaseRequest.name
            hostname == databaseRequest.hostname
            port == databaseRequest.port
            databaseName == databaseRequest.databaseName
            username == databaseRequest.username
            password == databaseRequest.password
        }
    }

    Database createDatabase() {
        def database = Database.builder()
                .name("main database")
                .hostname("10.50.15.62")
                .port(5558)
                .databaseName("master")
                .username("administrator")
                .password("veslo")
                .build()

        return databaseRepository.save(database)
    }

    def cleanup() {
        //primitive way of cleaning the DB
        databaseRepository.deleteAll()
    }
}
