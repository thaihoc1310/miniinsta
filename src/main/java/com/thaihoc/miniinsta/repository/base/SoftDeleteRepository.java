package com.thaihoc.miniinsta.repository.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@NoRepositoryBean
public interface SoftDeleteRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = true WHERE e.id = :id")
    void softDelete(ID id);

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = false WHERE e.id = :id")
    void restore(ID id);

    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = true AND e.id = :id")
    T findDeletedById(ID id);

    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.deleted = false")
    long countActive();
}