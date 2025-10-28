package com.example.version1.repository;

import com.example.version1.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryUbicacion extends JpaRepository<Ubicacion, Long> {

    @Query("select u from Ubicacion u left join fetch u.locales where u.id = :id")
    Ubicacion findByIdWithLocales(@Param("id") Long id);
}
