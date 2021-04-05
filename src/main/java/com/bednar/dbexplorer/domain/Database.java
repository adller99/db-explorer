package com.bednar.dbexplorer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.time.LocalDateTime;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Database {

    private static final String SQ_GENERATOR = "SQ_DATABASE";

    @Id
    @SequenceGenerator(allocationSize = 1, name = SQ_GENERATOR)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SQ_GENERATOR)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String hostname;

    @Column(nullable = false)
    private int port;

    @Column(nullable = false)
    private String databaseName;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime created;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime lastModified;
}
