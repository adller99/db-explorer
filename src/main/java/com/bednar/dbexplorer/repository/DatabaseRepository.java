package com.bednar.dbexplorer.repository;

import com.bednar.dbexplorer.domain.Database;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatabaseRepository extends JpaRepository<Database, Long> {

}
