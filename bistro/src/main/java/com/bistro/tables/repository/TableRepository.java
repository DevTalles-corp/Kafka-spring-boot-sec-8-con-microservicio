package com.bistro.tables.repository;

import com.bistro.tables.model.Table;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<Table, Long> {

    List<Table> findByCapacityGreaterThanEqualOrderByCapacityAsc(int capacity);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Table t WHERE t.id = :id")
    Optional<Table> findByIdForUpdate(Long id);
}
